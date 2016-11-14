package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.sos.Storage;
import uk.ac.standrews.cs.sos.model.locations.LocationUtility;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.manifests.atom.AtomStorage;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 *
 * TODO - replication
 */
public class SOSStorage implements Storage {

    private ManifestsManager manifestsManager;

    private AtomStorage atomStorage;

    public SOSStorage(Node node, LocalStorage storage, ManifestsManager manifestsManager) {

        this.manifestsManager = manifestsManager;
        atomStorage = new AtomStorage(node.getNodeGUID(), storage);
    }

    @Override
    public Atom addAtom(Location location) throws StorageException, ManifestPersistException {

        Collection<LocationBundle> bundles = new ArrayList<>();
        bundles.add(new ProvenanceLocationBundle(location));

        IGUID guid = store(location, bundles);
        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public Atom addAtom(InputStream inputStream)
            throws ManifestPersistException, StorageException {

        Collection<LocationBundle> bundles = new ArrayList<>();
        IGUID guid = store(inputStream, bundles);
        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    /**
     * Return an InputStream for the given Atom.
     * The caller should ensure that the stream is closed.
     *
     * @param atom describing the atom to retrieve.
     * @return
     */
    @Override
    public InputStream getAtomContent(Atom atom) {
        InputStream dataStream = null;
        Collection<LocationBundle> locations = atom.getLocations();
        for(LocationBundle location:locations) {

            try {
                dataStream = LocationUtility.getInputStreamFromLocation(location.getLocation());
            } catch (SourceLocationException e) {
                continue;
            }

            if (dataStream != null) {
                break;
            }
        }

        return dataStream;
    }

    @Override
    public InputStream getAtomContent(IGUID guid) {
        try {
            Manifest manifest = manifestsManager.findManifest(guid);

            if (manifest instanceof Atom) { // TODO - this comparison could be improved, with the manifest returning the type
                Atom atom = (Atom) manifest;
                return getAtomContent(atom);
            }
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected IGUID store(Location location, Collection<LocationBundle> bundles) throws StorageException {
        return atomStorage.persistAtomAndUpdateLocationBundles(location, bundles); // FIXME - this should undo the cache locations!
    }

    protected IGUID store(InputStream inputStream, Collection<LocationBundle> bundles) throws StorageException {
        return atomStorage.persistAtomAndUpdateLocationBundles(inputStream, bundles);
    }
}
