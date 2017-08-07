package uk.ac.standrews.cs.sos.impl.services;

import org.apache.commons.io.input.NullInputStream;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.data.AtomStorage;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
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
import uk.ac.standrews.cs.utilities.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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
    public Atom addAtom(AtomBuilder atomBuilder, boolean persist) throws DataStorageException, ManifestPersistException {
        Set<LocationBundle> bundles = new LinkedHashSet<>();

        IGUID guid = addAtom(atomBuilder, bundles, persist);
        if (guid == null || guid.isInvalid()) {
            throw new DataStorageException();
        }

        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        dataDiscoveryService.addManifest(manifest);

        return manifest;
    }

    public SecureAtomManifest addSecureAtom(AtomBuilder atomBuilder, Role role) throws StorageException, ManifestPersistException, ManifestNotMadeException, DataStorageException, IOException {
        Set<LocationBundle> bundles = new LinkedHashSet<>();

        IGUID guid = addAtom(atomBuilder, bundles, false);
        if (guid == null || guid.isInvalid()) {
            throw new StorageException();
        }

        SecureAtomManifest manifest = ManifestFactory.createSecureAtomManifest(guid, bundles, role);
        saveData(manifest);
        dataDiscoveryService.addManifest(manifest);

        return manifest;
    }

    // Secure an already existing atom manifest and its data
    public SecureAtomManifest secureAtom(Atom atom, Role role, boolean persist) throws StorageException, ManifestPersistException, ManifestNotMadeException {
        Set<LocationBundle> bundles = new LinkedHashSet<>();

//        IGUID guid = addAtom(atomBuilder, bundles, persist);
//        if (guid == null || guid.isInvalid()) {
//            throw new StorageException();
//        }
//
//        SecureAtomManifest manifest = ManifestFactory.createSecureAtomManifest(guid, bundles, role);
//        // TODO - save encrypted data
//        dataDiscoveryService.addManifest(manifest);

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
     * FIXME - use GUID as param
     *
     * @param atom describing the atom to retrieve.
     * @return data referenced by the atom
     */
    @Override
    public InputStream getAtomContent(Atom atom) {
        InputStream dataStream = null;

        Iterator<LocationBundle> it = findLocations(atom.guid());
        while(it.hasNext()) {
            LocationBundle locationBundle = it.next();

            Location location = locationBundle.getLocation();
            dataStream = LocationUtility.getInputStreamFromLocation(location);

            if (!(dataStream instanceof NullInputStream)) {
                break;
            }
        }

        return dataStream;
    }

    @Override
    public InputStream getAtomContent(IGUID guid) throws AtomNotFoundException {
        try {
            Manifest manifest = dataDiscoveryService.getManifest(guid);

            if (manifest.getType() == ManifestType.ATOM) {
                Atom atom = (Atom) manifest;
                return getAtomContent(atom);
            }
        } catch (ManifestNotFoundException e) {
            throw new AtomNotFoundException();
        }

        return new NullInputStream(0);
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
     * @param persist
     * @return
     * @throws StorageException
     */
    private IGUID addAtom(AtomBuilder atomBuilder, Set<LocationBundle> bundles, boolean persist) throws DataStorageException {

        Pair<IGUID, LocationBundle> retval;
        if (persist) {
            retval = atomStorage.persist(atomBuilder);
        } else {
            retval = atomStorage.cache(atomBuilder);
        }

        if (atomBuilder.isLocation()) {
            Location provenanceLocation = atomBuilder.getLocation();
            bundles.add(new ProvenanceLocationBundle(provenanceLocation));
        }

        if (bundles != null) {
            bundles.add(retval.Y());
            addLocation(retval.X(), retval.Y());
        }

        return retval.X();
    }

    /**
     * Saves the data to disk
     *
     * @param secureAtomManifest
     * @throws DataStorageException
     * @throws PersistenceException
     * @throws IOException
     */
    private void saveData(SecureAtomManifest secureAtomManifest) throws DataStorageException, PersistenceException, IOException {

        IDirectory dataDirectory = storage.getDataDirectory();
        IFile file = storage.createFile(dataDirectory, secureAtomManifest.guid().toMultiHash(), secureAtomManifest.getDataO());
        file.persist();
    }


}
