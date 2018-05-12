/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.EmptyData;
import uk.ac.standrews.cs.castore.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.DataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.IgnoreException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.data.AtomStorage;
import uk.ac.standrews.cs.sos.impl.data.StoredAtomInfo;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.CompoundBuilder;
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
import uk.ac.standrews.cs.sos.impl.protocol.tasks.AtomReplication;
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

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Internals.LOCATIONS_INDEX_FILE;
import static uk.ac.standrews.cs.sos.impl.datamodel.directory.LocationsIndexImpl.comparator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSStorageService implements StorageService {

    // Settings for this service
    private final SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings storageSettings;
    private final IGUID localNodeGUID;

    // Other node services
    private final ManifestsDataService manifestsDataService;
    private final NodeDiscoveryService nodeDiscoveryService;

    // Internal storage/cache/index/etc
    private final LocalStorage storage;
    private AtomStorage atomStorage;
    private LocationsIndex locationIndex;

    public SOSStorageService(SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings storageSettings, IGUID localNodeGUID, LocalStorage storage,
                             ManifestsDataService manifestsDataService, NodeDiscoveryService nodeDiscoveryService) throws ServiceException {

        this.storageSettings = storageSettings;
        this.localNodeGUID = localNodeGUID;

        this.storage = storage;
        this.manifestsDataService = manifestsDataService;
        this.nodeDiscoveryService = nodeDiscoveryService;

        loadOrCreateLocationIndex();

        atomStorage = new AtomStorage(localNodeGUID, storage);
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder) throws DataStorageException, ManifestPersistException {

        Set<LocationBundle> bundles = new TreeSet<>(comparator());

        StoredAtomInfo storedAtomInfo;
        if (atomBuilder.isDoNotStoreDataLocally()) {
            // Data is not stored, but GUID must be calculated anyway
            storedAtomInfo = generateGUIDOnly(atomBuilder);
        } else {
            // The atom will be encrypted using the Role of the atom builder
            storedAtomInfo = addAtom(atomBuilder, bundles);
        }

        IGUID guid = storedAtomInfo.getGuid();
        if (guid == null || guid.isInvalid()) {
            throw new DataStorageException();
        }

        Atom atom;
        if (atomBuilder.isAlreadyProtected()) {
            // Keys are unknown
            atom = ManifestFactory.createSecureAtomManifest(guid, bundles);

        } else if (atomBuilder.isProtect()) {
            HashMap<IGUID, String> rolesToKeys = new HashMap<>();
            rolesToKeys.put(storedAtomInfo.getRole(), storedAtomInfo.getEncryptedKey());
            atom = ManifestFactory.createSecureAtomManifest(guid, bundles, rolesToKeys);

        } else {
            atom = ManifestFactory.createAtomManifest(guid, bundles);

        }

        if (!atomBuilder.isDoNotStoreManifestLocally()) {
            manifestsDataService.addManifest(atom);
        }

        int replicationFactor = atomBuilder.getReplicationFactor() <= storageSettings.getMaxReplication() ? atomBuilder.getReplicationFactor() : storageSettings.getMaxReplication();
        if (replicationFactor > 0) {

            try (Data data = atomBuilder.getData()){

                NodesCollection codomain = atomBuilder.getReplicationNodes();
                boolean sequentialReplication = storageSettings.isSequentialReplication();

                long start = System.nanoTime();
                AtomReplication atomReplication = new AtomReplication(guid, data, codomain, replicationFactor,
                        this, nodeDiscoveryService,
                        atomBuilder.isDelegateReplication(), atomBuilder.isAlreadyProtected(), sequentialReplication);
                TasksQueue.instance().performAsyncTask(atomReplication);
                long duration = System.nanoTime() - start;
                InstrumentFactory.instance().measure(StatsTYPE.io, StatsTYPE.replicate_atom, Long.toString(atomBuilder.getData().getSize()), Boolean.toString(sequentialReplication), duration, replicationFactor);

            } catch (IOException | SOSProtocolException e) {
                SOS_LOG.log(LEVEL.ERROR, "Error occurred while attempting to replicate atom " + guid.toShortString() + " to other storage nodes");
                throw new DataStorageException("Error occurred while attempting to replicate atom " + guid.toShortString() + " to other storage nodes");
            }
        }

        return atom;
    }

    @Override
    public List<Atom> addAtom(CompoundBuilder compoundBuilder) throws DataStorageException, ManifestPersistException {

        if (compoundBuilder.getType() != CompoundType.DATA) throw new DataStorageException();

        // WORK IN PROGRESS
        // Add data in chunks
        return null;
    }

    @Override
    public void deleteAtom(IGUID guid) throws AtomNotFoundException {

        try {
            IDirectory dataDirectory = storage.getAtomsDirectory();
            dataDirectory.remove(guid.toMultiHash());

            locationIndex.deleteLocation(localNodeGUID, guid);
            manifestsDataService.deleteLocalLocation(guid);

        } catch (DataStorageException | BindingAbsentException e) {
            throw new AtomNotFoundException(guid);
        }

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

        } catch (ManifestPersistException e) {
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
            throw new AtomNotFoundException(atom.guid());
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

        } catch (IOException | AtomNotFoundException | ProtectionException e) {
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
            throw new AtomNotFoundException(guid);
        }

        return new EmptyData();
    }

    @Override
    public Data getAtomContent(NodesCollection nodesCollection, IGUID guid) throws AtomNotFoundException {

        try {
            Manifest manifest = manifestsDataService.getManifest(nodesCollection, NodeType.MDS, guid);

            if (manifest.getType() == ManifestType.ATOM || manifest.getType() == ManifestType.ATOM_PROTECTED) {
                Atom atom = (Atom) manifest;
                return getAtomContent(nodesCollection, atom);
            }
        } catch (ManifestNotFoundException e) {
            throw new AtomNotFoundException(guid);
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
    public boolean atomExists(IGUID guid) {

        try {
            IDirectory dataDirectory = storage.getAtomsDirectory();

            return dataDirectory.contains(guid.toMultiHash());
        } catch (DataStorageException e) {
            return false;
        }
    }

    @Override
    public IGUID challenge(IGUID guid, String challenge) {

        try {
            Data data = getAtomContent(guid);

            if (data instanceof EmptyData) return new InvalidID();

            List<InputStream> streams = Arrays.asList(data.getInputStream(), new ByteArrayInputStream(challenge.getBytes()));
            InputStream combinedStream = new SequenceInputStream(Collections.enumeration(streams));

            return GUIDFactory.generateGUID(GUID_ALGORITHM, combinedStream);

        } catch (AtomNotFoundException | GUIDGenerationException e) {
            return new InvalidID();
        }
    }

    @Override
    public void flush() {

        try {
            IDirectory nodeDir = storage.getNodeDirectory();
            IFile file = storage.createFile(nodeDir, LOCATIONS_INDEX_FILE);
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

            // Filter SOS location against nodesCollection
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

            Data data = LocationUtility.getData(location);

            if (!(data instanceof EmptyData)) {

                long duration = System.nanoTime() - start;
                InstrumentFactory.instance().measure(StatsTYPE.io, StatsTYPE.read_atom, Long.toString(data.getSize()), duration);

                return data;
            }
        }

        throw new AtomNotFoundException(atom.guid());
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
            retval = atomStorage.store(atomBuilder, BundleTypes.PERSISTENT);
        } else {
            retval = atomStorage.store(atomBuilder, BundleTypes.CACHE);
        }

        if (atomBuilder.isSetLocationAndProvenance() && atomBuilder.isLocation()) {
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

    private StoredAtomInfo generateGUIDOnly(AtomBuilder atomBuilder) throws DataStorageException {

        StoredAtomInfo storedAtomInfo = new StoredAtomInfo();

        try (Data data = atomBuilder.getData();
             InputStream inputStream = data.getInputStream()) {

            IGUID guid = GUIDFactory.generateGUID(inputStream);
            storedAtomInfo.setGuid(guid);
        } catch (IOException | GUIDGenerationException e) {
            throw new DataStorageException("Unable to generate GUID for data");
        }

        return storedAtomInfo;
    }

    private void loadOrCreateLocationIndex() throws ServiceException {

        // Load/Create the locations Index impl
        try {
            IDirectory cacheDir = storage.getNodeDirectory();
            IFile file = storage.createFile(cacheDir, LOCATIONS_INDEX_FILE);
            if (file.exists()) {
                locationIndex = (LocationsIndex) Persistence.load(file);
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            throw new ServiceException(ServiceException.SERVICE.STORAGE, "Unable to create the LocationIndex");
        } catch (IgnoreException e) {
            SOS_LOG.log(LEVEL.WARN, "Ignore exception on locations index loading");
        }

        if (locationIndex == null) {
            locationIndex = new LocationsIndexImpl();
        }
    }
}
