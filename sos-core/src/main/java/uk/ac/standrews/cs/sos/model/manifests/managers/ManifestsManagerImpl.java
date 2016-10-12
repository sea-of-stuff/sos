package uk.ac.standrews.cs.sos.model.manifests.managers;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotSetException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.manifests.managers.ManifestsManager;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.util.stream.Stream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManagerImpl implements ManifestsManager {

    private PolicyManager policyManager;

    private LocalManifestsManager local;
    private RemoteManifestsManager remote;

    public ManifestsManagerImpl(PolicyManager policyManager, InternalStorage internalStorage,
                                NodeManager nodeManager, RequestsManager requestsManager) {
        this.policyManager = policyManager;

        local = new LocalManifestsManager(internalStorage);
        remote = new RemoteManifestsManager(policyManager, nodeManager, requestsManager);
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {
        local.addManifest(manifest);
        remote.addManifest(manifest);
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {
        return local.findManifest(guid);
    }

    @Override
    public Stream<Manifest> getAllManifests() {
        return local.getAllManifests();
    }

    @Override
    public Version getHEAD(IGUID invariant) throws HEADNotFoundException {
        return local.getHEAD(invariant);
    }

    @Override
    public void setHEAD(IGUID version) throws HEADNotSetException {
        local.setHEAD(version);
    }
}