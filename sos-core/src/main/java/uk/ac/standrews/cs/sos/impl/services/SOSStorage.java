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
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.data.AtomStorage;
import uk.ac.standrews.cs.sos.impl.data.StoredAtomInfo;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.directory.LocationsIndexImpl;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.ExternalLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.DataReplication;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.model.*;
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
import java.util.stream.Collectors;

import static uk.ac.standrews.cs.sos.impl.datamodel.directory.LocationsIndexImpl.comparator;

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

        Set<LocationBundle> bundles = new TreeSet<>(comparator());

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
                DataReplication dataReplication = new DataReplication(manifest.guid(), atomBuilder.getData(), atomBuilder.getReplicationNodes(), replicationFactor, this, nodeDiscoveryService, atomBuilder.isDelegateReplication());
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

        Set<LocationBundle> bundles = new TreeSet<>(comparator());

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

    @Override
    public SecureAtom grantAccess(SecureAtom secureAtom, Role granterRole, Role granteeRole) throws ProtectionException {

        try {
            String encryptedKey = secureAtom.keysRoles().get(granterRole.guid());
            SecretKey key = granterRole.decrypt(encryptedKey);

            String granteeEncryptedKey = granteeRole.encrypt(key);

            secureAtom.addKeyRole(granteeRole.guid(), granteeEncryptedKey);
            dataDiscoveryService.addManifest(secureAtom);

            return secureAtom;
        } catch (Exception e) {
            throw new ProtectionException(e);
        }
    }


    /**
     * Return an InputStream for the given Atom.
     * The caller should ensure that the stream is closed.
     *
     * @param atom describing the atom to retrieve.
     * @return data referenced by the atom
     */
    @Override
    public Data getAtomContent(Atom atom) throws AtomNotFoundException {

        try {
            return getAtomContent(new NodesCollectionImpl(NodesCollectionType.ANY), atom);
        } catch (NodesCollectionException e) {
            throw new AtomNotFoundException();
        }

    }

    private Data getAtomContent(NodesCollection nodesCollection, Atom atom) throws AtomNotFoundException {

        Set<IGUID> nodeRefs = nodesCollection.nodesRefs();

        for (LocationBundle locationBundle : findLocations(atom)) {
            Location location = locationBundle.getLocation();

            if (location instanceof SOSLocation) {

                if (nodesCollection.type() == NodesCollectionType.SPECIFIED) {

                    if (!nodeRefs.contains(((SOSLocation) location).getMachineID())) {
                        continue;
                    }

                } else if (nodesCollection.type() == NodesCollectionType.LOCAL) {

                    if (!((SOSLocation) location).getMachineID().equals(nodeDiscoveryService.getThisNode().getNodeGUID())) {
                        continue;
                    }

                }

            } else {

                if (nodesCollection.type() == NodesCollectionType.LOCAL || nodesCollection.type() == NodesCollectionType.SPECIFIED) {
                    continue;
                }
            }

            Data data = LocationUtility.getDataFromLocation(location);

            if (!(data instanceof EmptyData)) {
                return data;
            }
        }

        throw new AtomNotFoundException();
    }

    @Override
    public Data getSecureAtomContent(SecureAtom atom, Role role) throws DataNotFoundException {

        try (Data encryptedData = getAtomContent(atom.guid())){

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

            if (manifest.getType() == ManifestType.ATOM || manifest.getType() == ManifestType.ATOM_PROTECTED) {
                Atom atom = (Atom) manifest;
                return getAtomContent(atom);
            }
        } catch (ManifestNotFoundException e) {
            throw new AtomNotFoundException();
        }

        return new EmptyData();
    }

    @Override
    public Data getAtomContent(NodesCollection nodesCollection, IGUID guid) throws AtomNotFoundException {

        try {
            Manifest manifest = dataDiscoveryService.getManifest(nodesCollection, guid);

            if (manifest.getType() == ManifestType.ATOM || manifest.getType() == ManifestType.ATOM_PROTECTED) {
                Atom atom = (Atom) manifest;
                return getAtomContent(nodesCollection, atom);
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
    public Queue<LocationBundle> findLocations(Atom atom) {
        Queue<LocationBundle> locationBundles = new PriorityQueue<>(comparator());
        locationBundles.addAll(locationIndex.findLocations(atom.guid()));
        locationBundles.addAll(atom.getLocations());

        Queue<LocationBundle> retval = new PriorityQueue<>(comparator());
        retval.addAll(
                locationBundles.stream()
                        .distinct()
                        .collect(Collectors.toList())
        );
        return retval;
    }

    @Override
    public Queue<LocationBundle> findLocations(IGUID guid) {

        Queue<LocationBundle> locationBundles = new PriorityQueue<>(comparator());
        locationBundles.addAll(locationIndex.findLocations(guid));

        try {
            Manifest manifest = dataDiscoveryService.getManifest(guid);
            if (manifest.getType().equals(ManifestType.ATOM) || manifest.getType().equals(ManifestType.ATOM_PROTECTED)) {
                Atom atom = (Atom) manifest;
                Queue<LocationBundle> locs = findLocations(atom);
                locationBundles.addAll(locs);
            }

        } catch (ManifestNotFoundException ignored) { }

        Queue<LocationBundle> retval = new PriorityQueue<>(comparator());
        retval.addAll(
                locationBundles.stream()
                        .distinct()
                        .collect(Collectors.toList())
        );
        return retval;
    }

    @Override
    public IGUID challenge(IGUID guid, String challenge) {

        try {
            Data data = getAtomContent(guid);

            if (data instanceof EmptyData) return new InvalidID();

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
