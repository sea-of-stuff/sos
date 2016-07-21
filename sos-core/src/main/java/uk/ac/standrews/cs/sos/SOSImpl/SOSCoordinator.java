package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.sos.Coordinator;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCoordinator implements Coordinator {

    protected Identity identity;
    protected ManifestsManager manifestsManager;

    private NodeManager nodeManager;

    // TODO - pass storage (this is needed to be used by manifest manager or just give it to the manifest manager ????)
    public SOSCoordinator(ManifestsManager manifestsManager, Identity identity, NodeManager nodeManager) {

        this.manifestsManager = manifestsManager;
        this.identity = identity;

        this.nodeManager = nodeManager;
    }

    @Override
    public PolicyManager getPolicyManager() {
        return null;
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
    public boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationException {
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
