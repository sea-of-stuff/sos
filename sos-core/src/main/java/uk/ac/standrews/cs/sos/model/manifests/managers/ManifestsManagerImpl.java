package uk.ac.standrews.cs.sos.model.manifests.managers;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.sos.storage.InternalStorage;

import java.util.stream.Stream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManagerImpl implements ManifestsManager {

    private ManifestsCache cache;
    private LocalManifestsManager local;
    private RemoteManifestsManager remote;

    public ManifestsManagerImpl(PolicyManager policyManager, InternalStorage internalStorage,
                                NodeManager nodeManager) {

        cache = new ManifestsCacheImpl();
        local = new LocalManifestsManager(internalStorage);
        remote = new RemoteManifestsManager(policyManager, nodeManager);
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {
        cache.addManifest(manifest);
        local.addManifest(manifest);
        remote.addManifest(manifest);
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

        Manifest manifest;
        try {
            manifest = cache.getManifest(guid);
        } catch (ManifestsCacheMissException e) {
            manifest = local.findManifest(guid);
            cache.addManifest(manifest);
        }

        // TODO - CHECK REMOTE

        return manifest;
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
