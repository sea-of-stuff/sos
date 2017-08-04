package uk.ac.standrews.cs.sos.impl.services;

import org.apache.commons.io.input.NullInputStream;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.BASE;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.manifests.SecureAtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.atom.AtomStorage;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

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

    public SOSStorage(IGUID localNodeGUID, LocalStorage storage, DataDiscoveryService dataDiscoveryService) {
        this.storage = storage;
        this.dataDiscoveryService = dataDiscoveryService;

        atomStorage = new AtomStorage(localNodeGUID, storage);
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder, boolean persist) throws StorageException, ManifestPersistException {
        Set<LocationBundle> bundles = new LinkedHashSet<>();

        IGUID guid = addAtom(atomBuilder, bundles, persist);
        if (guid == null || guid.isInvalid()) {
            throw new StorageException();
        }

        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        dataDiscoveryService.addManifest(manifest);

        return manifest;
    }

    public SecureAtomManifest addSecureAtom(AtomBuilder atomBuilder, Role role, boolean persist) throws StorageException, ManifestPersistException, ManifestNotMadeException, DataStorageException, IOException {
        Set<LocationBundle> bundles = new LinkedHashSet<>();

        IGUID guid = addAtom(atomBuilder, bundles, persist);
        if (guid == null || guid.isInvalid()) {
            throw new StorageException();
        }

        SecureAtomManifest manifest = ManifestFactory.createSecureAtomManifest(guid, bundles, role);
        saveData(manifest);
        dataDiscoveryService.addManifest(manifest);

        return manifest;
    }

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

        Iterator<LocationBundle> it = atomStorage.getLocationsIterator(atom.guid());
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
        atomStorage.addLocation(guid, locationBundle);
    }

    @Override
    public Iterator<LocationBundle> findLocations(IGUID guid) {
        return atomStorage.getLocationsIterator(guid);
    }

    @Override
    public boolean challenge(IGUID guid, String challenge) {
        return false;
    }

    @Override
    public void flush() {

        try {
            atomStorage.flush();
        } catch (DataStorageException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to flush the node internal storage");
        }
    }

    private IGUID addAtom(AtomBuilder atomBuilder, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        IGUID guid;
        if (atomBuilder.isLocation()) {
            guid = addAtomByLocation(atomBuilder, bundles, persist);
        } else if (atomBuilder.isInputStream()) {
            guid = addAtomByStream(atomBuilder, bundles, persist);
        } else {
            throw new StorageException("AtomBuilder has not been set correctly");
        }

        return guid;
    }

    private IGUID addAtomByLocation(AtomBuilder atomBuilder, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        Location location = atomBuilder.getLocation();
        bundles.add(new ProvenanceLocationBundle(location));
        return store(location, bundles, persist);
    }

    private IGUID addAtomByStream(AtomBuilder atomBuilder, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        InputStream inputStream = atomBuilder.getInputStream();
        return store(inputStream, bundles, persist);
    }

    private IGUID store(Location location, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        if (persist) {
            return atomStorage.persistAtomAndUpdateLocationBundles(location, bundles); // FIXME - this should undo the cache locations(and indeX)
        } else {
            return atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        }
    }

    private IGUID store(InputStream inputStream, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        if (persist) {
            return atomStorage.persistAtomAndUpdateLocationBundles(inputStream, bundles);
        } else {
            return atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        }
    }

    private void saveData(SecureAtomManifest secureAtomManifest) throws DataStorageException, PersistenceException, IOException {

        Data data = new InputStreamData(secureAtomManifest.getData());
        IDirectory dataDirectory = storage.getDataDirectory();
        IFile file = storage.createFile(dataDirectory, secureAtomManifest.guid().toMultiHash(BASE.HEX), data);
        file.persist();
    }


}
