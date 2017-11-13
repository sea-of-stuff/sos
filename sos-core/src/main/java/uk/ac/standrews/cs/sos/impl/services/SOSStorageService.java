package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.EmptyData;
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
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.services.StorageService;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.*;
import java.util.stream.Collectors;

import static uk.ac.standrews.cs.sos.constants.Internals.LOCATIONS_INDEX_FILE;
import static uk.ac.standrews.cs.sos.impl.datamodel.directory.LocationsIndexImpl.comparator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSStorageService implements StorageService {

    // Settings for this service
    private SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings storageSettings;

    // Other node services
    private ManifestsDataService manifestsDataService;
    private NodeDiscoveryService nodeDiscoveryService;

    // Internal storage/cache/index/etc
    private LocalStorage storage;
    private AtomStorage atomStorage;
    private LocationsIndex locationIndex;

    public SOSStorageService(SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings storageSettings, IGUID localNodeGUID, LocalStorage storage,
                             ManifestsDataService manifestsDataService, NodeDiscoveryService nodeDiscoveryService) throws ServiceException {

        this.storageSettings = storageSettings;

        this.storage = storage;
        this.manifestsDataService = manifestsDataService;
        this.nodeDiscoveryService = nodeDiscoveryService;

        loadOrCreateLocationIndex();

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
        manifestsDataService.addManifest(manifest);

        // We subtract 1 from the builder replication factor, because the atom was already added to this node (which makes one of the replicas)
        int replicationFactor = (atomBuilder.getReplicationFactor() - 1) <= storageSettings.getMaxReplication() ? (atomBuilder.getReplicationFactor() - 1) : storageSettings.getMaxReplication();
        if (replicationFactor > 0) {

            try (Data data = atomBuilder.getData()){
                DataReplication dataReplication = new DataReplication(manifest.guid(), data, atomBuilder.getReplicationNodes(), replicationFactor, this, nodeDiscoveryService, atomBuilder.isDelegateReplication());
                TasksQueue.instance().performAsyncTask(dataReplication);

            } catch (SOSProtocolException e) {
                SOS_LOG.log(LEVEL.ERROR, "Error occurred while attempting to replicate atom " + guid + " to other storage nodes");
            } catch (Exception e) {
                SOS_LOG.log(LEVEL.ERROR, "General exception occurred while attempting to replicate atom " + guid + " to other storage nodes");
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
            Role role = atomBuilder.getRole();
            if (role == null) throw new RoleNotFoundException();

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
        manifestsDataService.addManifest(manifest);

        return manifest;
    }

    @Override
    public SecureManifest grantAccess(SecureManifest secureManifest, Role granterRole, Role granteeRole) throws ProtectionException {

        try {
            String encryptedKey = secureManifest.keysRoles().get(granterRole.guid());
            SecretKey key = granterRole.decrypt(encryptedKey);

            String granteeEncryptedKey = granteeRole.encrypt(key);

            secureManifest.addKeyRole(granteeRole.guid(), granteeEncryptedKey);
            manifestsDataService.addManifest(secureManifest);

            return secureManifest;
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
            Manifest manifest = manifestsDataService.getManifest(guid);

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
            Manifest manifest = manifestsDataService.getManifest(nodesCollection, NodeType.DDS, guid);

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
            Manifest manifest = manifestsDataService.getManifest(guid);
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
            IFile file = storage.createFile(cacheDir, LOCATIONS_INDEX_FILE);
            locationIndex.persist(file);
        } catch (IOException | DataStorageException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to flush LocationIndex");
        }
    }

    @Override
    public void shutdown() {
        locationIndex.clear();
    }

    @Override
    public SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings getStorageSettings() {
        return storageSettings;
    }

    private Data getAtomContent(NodesCollection nodesCollection, Atom atom) throws AtomNotFoundException {

        long start = System.nanoTime();

        Set<IGUID> nodeRefs = nodesCollection.nodesRefs();

        for (LocationBundle locationBundle : findLocations(atom)) {
            Location location = locationBundle.getLocation();

            if (location instanceof SOSLocation) {

                if (nodesCollection.type() == NodesCollectionType.SPECIFIED) {

                    if (!nodeRefs.contains(((SOSLocation) location).getMachineID())) {
                        continue;
                    }

                } else if (nodesCollection.type() == NodesCollectionType.LOCAL) {

                    IGUID locationMachineID = ((SOSLocation) location).getMachineID();
                    Node localNode = nodeDiscoveryService.getThisNode();
                    if (!locationMachineID.equals(localNode.guid())) {
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

                long duration = System.nanoTime() - start;
                InstrumentFactory.instance().measure(StatsTYPE.io, StatsTYPE.read_atom, Long.toString(data.getSize()), duration);

                return data;
            }
        }

        throw new AtomNotFoundException();
    }


    /**
     * Adds the data part of the atom to the SOS
     *
     * @param atomBuilder containing the information about the atom to be added
     * @param bundles (OUT) containing the existing locations of the atom. This will be updated to contain the new locations.
     * @return atom info
     * @throws DataStorageException if the data could not be saved to disk
     */
    private StoredAtomInfo addAtom(AtomBuilder atomBuilder, Set<LocationBundle> bundles) throws DataStorageException {

        long start = System.nanoTime();

        StoredAtomInfo retval;
        if (storageSettings.isCanPersist() && atomBuilder.getBundleType() == BundleTypes.PERSISTENT) {
            retval = atomStorage.persist(atomBuilder);
        } else {
            retval = atomStorage.cache(atomBuilder);
        }

        if (atomBuilder.isLocation()) {
            Location location = atomBuilder.getLocation();
            bundles.add(new ExternalLocationBundle(location));
        }

        if (bundles != null) {
            bundles.add(retval.getLocationBundle());
            addLocation(retval.getGuid(), retval.getLocationBundle());
        }

        long duration = System.nanoTime() - start;
        InstrumentFactory.instance().measure(StatsTYPE.io, StatsTYPE.add_atom, Long.toString(atomBuilder.getData().getSize()), duration);

        return retval;
    }

    private void loadOrCreateLocationIndex() throws ServiceException {

        // Load/Create the locations Index impl
        try {
            IDirectory cacheDir = storage.getNodeDirectory();
            IFile file = storage.createFile(cacheDir, LOCATIONS_INDEX_FILE);
            if (file.exists()) {
                locationIndex = (LocationsIndex) Persistence.Load(file);
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            throw new ServiceException(ServiceException.SERVICE.STORAGE, "Unable to create the LocationIndex");
        }

        if (locationIndex == null) {
            locationIndex = new LocationsIndexImpl();
        }
    }
}
