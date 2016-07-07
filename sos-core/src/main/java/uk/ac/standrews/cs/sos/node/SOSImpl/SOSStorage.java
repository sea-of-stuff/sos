package uk.ac.standrews.cs.sos.node.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.Storage;
import uk.ac.standrews.cs.sos.model.datastore.StorageHelper;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSStorage implements Storage {

    protected InternalStorage storage;

    protected Identity identity;
    protected ManifestsManager manifestsManager;

    public SOSStorage(InternalStorage storage, ManifestsManager manifestsManager, Identity identity) {
        this.storage = storage;
        this.manifestsManager = manifestsManager;
        this.identity = identity;
    }

    @Override
    public Identity getIdentity() {
        return this.identity;
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

    @Override
    public Compound addCompound(CompoundType type, Collection<Content> contents)
            throws ManifestNotMadeException, ManifestPersistException {

        CompoundManifest manifest = ManifestFactory.createCompoundManifest(type, contents, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public Version addVersion(IGUID content,
                              IGUID invariant,
                              Collection<IGUID> prevs,
                              Collection<IGUID> metadata)
            throws ManifestNotMadeException, ManifestPersistException {

        VersionManifest manifest = ManifestFactory.createVersionManifest(content, invariant, prevs, metadata, identity);
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
                dataStream = StorageHelper.getInputStreamFromLocation(location.getLocation());
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
    public Node getNode(IGUID guid) {
        throw new UnsupportedOperationException();
    }

    protected IGUID store(Location location, Collection<LocationBundle> bundles) throws StorageException {
        return StorageHelper.persistAtomAndUpdateLocationBundles(storage, location, bundles); // NOTE - this might undo the cache locations!
    }

    protected IGUID store(InputStream inputStream, Collection<LocationBundle> bundles) throws StorageException {
        return StorageHelper.persistAtomAndUpdateLocationBundles(storage, inputStream, bundles);
    }
}
