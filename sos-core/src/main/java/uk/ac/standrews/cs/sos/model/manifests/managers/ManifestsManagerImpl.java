package uk.ac.standrews.cs.sos.model.manifests.managers;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.interfaces.manifests.*;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
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

    public ManifestsManagerImpl(ManifestPolicy manifestPolicy, InternalStorage internalStorage,
                                NodeManager nodeManager) {

        // TODO - load cache if there is one

        cache = new ManifestsCacheImpl();
        local = new LocalManifestsManager(internalStorage);
        remote = new RemoteManifestsManager(manifestPolicy, nodeManager);
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {
        cache.addManifest(manifest);
        local.addManifest(manifest);
        remote.addManifest(manifest);
    }

    @Override
    public void updateAtom(Atom atom) throws ManifestManagerException, ManifestNotFoundException {
        local.updateAtom(atom);
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

        if (guid == null || guid.isInvalid()) {
            throw new ManifestNotFoundException("GUID was invalid");
        }

        Manifest manifest = findManifestCache(guid);
        if (manifest == null) {
            manifest = findManifest(local, guid);
        }
        if (manifest == null) {
            manifest = findManifest(remote, guid);
        }
        if (manifest == null) {
            throw new ManifestNotFoundException("Unable to find manifest in cache, local, remote");
        }

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

    private Manifest findManifestCache(IGUID guid) {
        Manifest manifest = null;
        try {
            manifest = cache.getManifest(guid);
        } catch (ManifestsCacheMissException e) {
            System.out.println(e.getMessage());
        }
        return manifest;
    }

    private Manifest findManifest(ManifestsManager manager, IGUID guid) {
        Manifest manifest = null;
        try {
            manifest = manager.findManifest(guid);
            cache.addManifest(manifest);
        } catch (ManifestNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return manifest;
    }

}
