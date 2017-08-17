package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.EmptyData;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.DataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.data.AtomStorage;
import uk.ac.standrews.cs.sos.impl.data.StoredAtomInfo;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.locations.bundles.ExternalLocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.directory.LocationsIndexImpl;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.DataReplication;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSStorage implements Storage {

    private SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings storageSettings;

    private DataDiscoveryService dataDiscoveryService;
    private UsersRolesService usersRolesService;
    private NodeDiscoveryService nodeDiscoveryService;

    private LocalStorage storage;
    private AtomStorage atomStorage;
    private LocationsIndex locationIndex;

    public SOSStorage(SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings storageSettings, IGUID localNodeGUID, LocalStorage storage,
                      DataDiscoveryService dataDiscoveryService, UsersRolesService usersRolesService, NodeDiscoveryService nodeDiscoveryService) throws ServiceException {

        this.storageSettings = storageSettings;

        this.storage = storage;
        this.dataDiscoveryService = dataDiscoveryService;
        this.usersRolesService = usersRolesService;
        this.nodeDiscoveryService = nodeDiscoveryService;

        // Load/Create the locations Index impl
        try {
            IDirectory cacheDir = storage.getNodeDirectory();
            IFile file = storage.createFile(cacheDir, "locations.index");
            if (file.exists()) {
                locationIndex = (LocationsIndex) Persistence.Load(file);
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            throw new ServiceException("STORAGE - Unable to create the LocationIndex");
        }

        if (locationIndex == null) {
            locationIndex = new LocationsIndexImpl();
        }

        atomStorage = new AtomStorage(localNodeGUID, storage);
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder) throws DataStorageException, ManifestPersistException {

        Set<LocationBundle> bundles = new TreeSet<>(LocationsIndexImpl.comparator());

        IGUID guid = addAtom(atomBuilder, bundles).getGuid();
        if (guid == null || guid.isInvalid()) {
            throw new DataStorageException();
        }

        Atom manifest = ManifestFactory.createAtomManifest(guid, bundles);
        dataDiscoveryService.addManifest(manifest);

        // We subtract 1 from the builder replication factor, because the atom was already added to this node (which makes one of the replicas)
        int replicationFactor = (atomBuilder.getReplicationFactor() - 1) <= storageSettings.getMaxReplication() ? (atomBuilder.getReplicationFactor() - 1) : storageSettings.getMaxReplication();
        if (replicationFactor > 0) {

            try {
                DataReplication dataReplication = new DataReplication(atomBuilder.getData(), atomBuilder.getReplicationNodes(), replicationFactor, this, nodeDiscoveryService, atomBuilder.isDelegateReplication());
                TasksQueue.instance().performAsyncTask(dataReplication);

            } catch (SOSProtocolException e) {
                SOS_LOG.log(LEVEL.ERROR, "Error occurred while attempting to replicate atom " + guid + " to other storage nodes");
            }
        }

        return manifest;
    }

    @Override
    public SecureAtom addSecureAtom(AtomBuilder atomBuilder) throws ManifestPersistException, ManifestNotMadeException, DataStorageException {

        if (atomBuilder.getRole() == null)
            throw new DataStorageException();

        Set<LocationBundle> bundles = new TreeSet<>(LocationsIndexImpl.comparator());

        // Make sure that a role is being used
        try {
            Role role = usersRolesService.getRole(atomBuilder);
            atomBuilder.setRole(role);
        } catch (RoleNotFoundException e) {
            throw new ManifestNotMadeException("Unable to set Role when creating Secure Atom");
        }

        StoredAtomInfo storedAtomInfo = addAtom(atomBuilder, bundles); // The atom will be encrypted using the Role of the atom builder

        IGUID guid = storedAtomInfo.getGuid();
        if (guid == null || guid.isInvalid()) {
            throw new DataStorageException();
        }

        HashMap<IGUID, String> rolesToKeys = new HashMap<>();
        rolesToKeys.put(storedAtomInfo.getRole(), storedAtomInfo.getEncryptedKey());

        SecureAtom manifest = ManifestFactory.createSecureAtomManifest(guid, bundles, rolesToKeys);
        dataDiscoveryService.addManifest(manifest);

        return manifest;
    }

    /**
     * Return an InputStream for the given Atom.
     * The caller should ensure that the stream is closed.
     *
     * TODO - find other locations
     *
     * @param atom describing the atom to retrieve.
     * @return data referenced by the atom
     */
    @Override
    public Data getAtomContent(Atom atom) throws AtomNotFoundException {

        Iterator<LocationBundle> it = findLocations(atom.guid());
        while(it.hasNext()) {
            LocationBundle locationBundle = it.next();

            Location location = locationBundle.getLocation();
            Data data = LocationUtility.getDataFromLocation(location);

            if (!(data instanceof EmptyData)) {
                return data;
            }
        }

        throw new AtomNotFoundException();
    }

    public Data getSecureAtomContent(SecureAtom atom, Role role) throws DataNotFoundException {

        try (Data encryptedData = getAtomContent(atom)){

            if (atom.keysRoles().containsKey(role.guid())) {
                String encryptedKey = atom.keysRoles().get(role.guid());
                SecretKey decryptedKey = role.decrypt(encryptedKey);

                return atomStorage.decryptData(encryptedData, decryptedKey);
            } else {
                throw new ProtectionException("Role/key not available for secure atom with GUID " + atom.guid().toShortString());
            }

        } catch (Exception e) {
            throw new DataNotFoundException();
        }

    }

    @Override
    public Data getAtomContent(IGUID guid) throws AtomNotFoundException {

        try {
            Manifest manifest = dataDiscoveryService.getManifest(guid);

            if (manifest.getType() == ManifestType.ATOM) {
                Atom atom = (Atom) manifest;
                return getAtomContent(atom);
            }
        } catch (ManifestNotFoundException e) {
            throw new AtomNotFoundException();
        }

        return new EmptyData();
    }

    @Override
    public void addLocation(IGUID guid, LocationBundle locationBundle) {
        locationIndex.addLocation(guid, locationBundle);
    }

    @Override
    public Iterator<LocationBundle> findLocations(IGUID guid) {
        return locationIndex.findLocations(guid);
    }

    @Override
    public IGUID challenge(IGUID guid, String challenge) {

        try {
            Data data = getAtomContent(guid);

            List<InputStream> streams = Arrays.asList(data.getInputStream(), new ByteArrayInputStream(challenge.getBytes()));
            InputStream combinedStream = new SequenceInputStream(Collections.enumeration(streams));

            return GUIDFactory.generateGUID(ALGORITHM.SHA256, combinedStream);

        } catch (AtomNotFoundException | IOException | GUIDGenerationException e) {
            return new InvalidID();
        }
    }

    @Override
    public void flush() {

        try {
            IDirectory cacheDir = storage.getNodeDirectory();
            IFile file = storage.createFile(cacheDir, "locations.index");
            locationIndex.persist(file);
        } catch (IOException | DataStorageException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to flush LocationIndex");
        }
    }

    @Override
    public SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings getStorageSettings() {
        return storageSettings;
    }

    /**
     * Adds the data part of the atom to the SOS
     *
     * @param atomBuilder
     * @param bundles
     * @return
     * @throws StorageException
     */
    private StoredAtomInfo addAtom(AtomBuilder atomBuilder, Set<LocationBundle> bundles) throws DataStorageException {

        StoredAtomInfo retval;
        if (storageSettings.isCanPersist() && atomBuilder.getBundleType() == BundleTypes.PERSISTENT) {
            retval = atomStorage.persist(atomBuilder);
        } else {
            retval = atomStorage.cache(atomBuilder);
        }

        // FIXME DITTO AS COMMENT BELOW
        if (atomBuilder.isLocation()) {
            Location location = atomBuilder.getLocation();
            bundles.add(new ExternalLocationBundle(location));
        }

        // TODO - do this outside of this method
        if (bundles != null) {
            bundles.add(retval.getLocationBundle());
            addLocation(retval.getGuid(), retval.getLocationBundle());
        }

        return retval;
    }

}
