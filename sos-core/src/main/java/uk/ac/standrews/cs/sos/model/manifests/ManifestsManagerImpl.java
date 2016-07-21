package uk.ac.standrews.cs.sos.model.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.model.manifests.managers.LocalManifestsManager;
import uk.ac.standrews.cs.sos.model.manifests.managers.RemoteManifestsManager;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManagerImpl implements ManifestsManager {

    private LocalManifestsManager local;
    private RemoteManifestsManager remote;

    public ManifestsManagerImpl(InternalStorage internalStorage, Index index, NodeManager nodeManager) {
        local = new LocalManifestsManager(internalStorage, index);
        remote = new RemoteManifestsManager(nodeManager);
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {
        local.addManifest(manifest);
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {
        return local.findManifest(guid);
    }

    public Collection<IGUID> findManifestsByType(String type) throws ManifestNotFoundException {
        return local.findManifestsByType(type);
    }

    public Collection<IGUID> findVersions(IGUID guid) throws ManifestNotFoundException {
        return local.findVersions(guid);
    }

    public Collection<IGUID> findManifestsThatMatchLabel(String label) throws ManifestNotFoundException {
        return local.findManifestsThatMatchLabel(label);
    }
}
