package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.EmptyData;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.DataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.data.AtomStorage;
import uk.ac.standrews.cs.sos.impl.data.StoredAtomInfo;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.manifests.SecureAtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.directory.LocationsIndexImpl;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSStorage implements Storage {

    private DataDiscoveryService dataDiscoveryService;

    private LocalStorage storage;
    private AtomStorage atomStorage;
    private LocationsIndex locationIndex;

    public SOSStorage(IGUID localNodeGUID, LocalStorage storage, DataDiscoveryService dataDiscoveryService) throws ServiceException {
        this.storage = storage;
        this.dataDiscoveryService = dataDiscoveryService;

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

        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        dataDiscoveryService.addManifest(manifest);

        return manifest;
    }

    public SecureAtomManifest addSecureAtom(AtomBuilder atomBuilder) throws ManifestPersistException, ManifestNotMadeException, DataStorageException {

        if (atomBuilder.getRole() == null)
            throw new DataStorageException();

        Set<LocationBundle> bundles = new TreeSet<>(LocationsIndexImpl.comparator());
        StoredAtomInfo storedAtomInfo = addAtom(atomBuilder, bundles);

        IGUID guid = storedAtomInfo.getGuid();
        if (guid == null || guid.isInvalid()) {
            throw new DataStorageException();
        }

        HashMap<IGUID, String> rolesToKeys = new HashMap<>();
        rolesToKeys.put(storedAtomInfo.getRole(), storedAtomInfo.getEncryptedKey());

        SecureAtomManifest manifest = ManifestFactory.createSecureAtomManifest(guid, bundles, rolesToKeys);
        dataDiscoveryService.addManifest(manifest);

        return manifest;
    }

    // Secure an already existing atom manifest and its data
    public SecureAtomManifest secureAtom(Atom atom, Role role, boolean persist) throws StorageException, ManifestPersistException, ManifestNotMadeException {

        return null;
    }

    @Override
    public Atom addData(AtomBuilder atomBuilder, NodesCollection nodes, int replicationFactor) throws StorageException {

        // TODO - add the data to the nodes in the collection

        return null;
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
    public boolean challenge(IGUID guid, String challenge) {
        return false;
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
        if (atomBuilder.getBundleType() == BundleTypes.PERSISTENT) {
            retval = atomStorage.persist(atomBuilder);
        } else if (atomBuilder.getBundleType() == BundleTypes.CACHE) {
            retval = atomStorage.cache(atomBuilder);
        } else {
            throw new DataStorageException();
        }

        // FIXME DITTO AS COMMENT BELOW
        if (atomBuilder.isLocation()) {
            Location provenanceLocation = atomBuilder.getLocation();
            bundles.add(new ProvenanceLocationBundle(provenanceLocation));
        }

        // TODO - do this outside of this method
        if (bundles != null) {
            bundles.add(retval.getLocationBundle());
            addLocation(retval.getGuid(), retval.getLocationBundle());
        }

        return retval;
    }

}
