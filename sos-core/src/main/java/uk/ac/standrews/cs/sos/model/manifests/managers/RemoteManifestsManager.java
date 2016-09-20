package uk.ac.standrews.cs.sos.model.manifests.managers;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.manifests.managers.ManifestsManager;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.util.Collection;

/**
 * The remote manifest manager allows the node to replicate manifests to other nodes in the SOS
 * as well as finding manifests in the rest of the SOS
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteManifestsManager implements ManifestsManager {

    private NodeManager nodeManager;

    public RemoteManifestsManager(NodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {

    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {
        return null;
    }

    @Override
    public Version getLatest(IGUID guid) throws ManifestNotFoundException {
        return null;
    }

    @Override
    public Collection<IGUID> findManifestsByType(String type) throws ManifestNotFoundException {
        return null;
    }

    @Override
    public Collection<IGUID> findVersions(IGUID guid) throws ManifestNotFoundException {
        return null;
    }

    @Override
    public Collection<IGUID> findManifestsThatMatchLabel(String label) throws ManifestNotFoundException {
        return null;
    }
}
