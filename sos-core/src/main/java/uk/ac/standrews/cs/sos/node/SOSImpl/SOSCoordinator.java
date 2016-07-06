package uk.ac.standrews.cs.sos.node.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.node.Coordinator;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCoordinator extends SOSCommon implements Coordinator {

    private NodeManager nodeManager;

    public SOSCoordinator(Configuration configuration, ManifestsManager manifestsManager, Identity identity, NodeManager nodeManager) {
        super(configuration, null, manifestsManager, identity); // FIXME - not sure if passing null is a good idea!
        this.nodeManager = nodeManager;
    }

    @Override
    public Atom addAtom(Location location) throws StorageException, ManifestPersistException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Atom addAtom(InputStream inputStream) throws StorageException, ManifestPersistException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Compound addCompound(CompoundType type, Collection<Content> contents) throws ManifestNotMadeException, ManifestPersistException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Version addVersion(IGUID content, IGUID invariant, Collection<IGUID> prevs, Collection<IGUID> metadata) throws ManifestNotMadeException, ManifestPersistException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected IGUID store(Location location, Collection<LocationBundle> bundles) throws StorageException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected IGUID store(InputStream inputStream, Collection<LocationBundle> bundles) throws StorageException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getAtomContent(Atom atom) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        manifestsManager.addManifest(manifest);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {

        Manifest manifest = manifestsManager.findManifest(guid);
        // NOTE - might have to contact other coordinators!
        return manifest;
    }

    @Override
    public boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationFailedException {
        // TODO - how is this verified if identity is unknown?
        return false;
    }

    @Override
    public Collection<IGUID> findManifestByType(String type) throws ManifestNotFoundException {
        return manifestsManager.findManifestsByType(type);
    }

    @Override
    public Collection<IGUID> findManifestByLabel(String label) throws ManifestNotFoundException {
        return manifestsManager.findManifestsThatMatchLabel(label);
    }

    @Override
    public Collection<IGUID> findVersions(IGUID invariant) throws ManifestNotFoundException {
        return manifestsManager.findVersions(invariant);
    }

    @Override
    public Node getNode(IGUID guid) {
        return nodeManager.getNode(guid);
    }

}
