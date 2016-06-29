package uk.ac.standrews.cs.sos.node.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.datastore.StorageHelper;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.io.InputStream;
import java.util.Collection;

/**
 * Implementation class for the SeaOfStuff interface.
 * The purpose of this class is to delegate jobs to the appropriate manifests
 * of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSClient extends SOSCommon {

    public SOSClient(Configuration configuration, Storage storage, ManifestsManager manifestsManager,
                     Identity identity) {
        super(configuration, storage, manifestsManager, identity);
    }

    @Override
    public void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        manifestsManager.addManifest(manifest);

        // TODO - recursively look for other manifests to add to the NodeManager
        if (recursive) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
        Manifest manifest;
        try {
            manifest = manifestsManager.findManifest(guid);
        } catch (ManifestNotFoundException e) {
            throw new ManifestNotFoundException();
        }
        return manifest;
    }

    @Override
    public boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationFailedException {
        boolean ret;
        try {
            ret = manifest.verify(identity);
        } catch (GUIDGenerationException | DecryptionException e) {
            throw new ManifestVerificationFailedException();
        }

        return ret;
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
    protected IGUID store(Location location, Collection<LocationBundle> bundles) throws DataStorageException {
        return StorageHelper.cacheAtomAndUpdateLocationBundles(configuration, storage, location, bundles);
    }

    @Override
    protected IGUID store(InputStream inputStream, Collection<LocationBundle> bundles) throws DataStorageException {
        return StorageHelper.cacheAtomAndUpdateLocationBundles(configuration, storage, inputStream, bundles);
    }
}
