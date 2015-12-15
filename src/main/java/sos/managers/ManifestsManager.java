package sos.managers;

import org.json.JSONObject;
import sos.configurations.SeaConfiguration;
import sos.exceptions.ManifestCacheException;
import sos.exceptions.ManifestPersistException;
import sos.exceptions.ManifestSaveException;
import sos.exceptions.UnknownManifestTypeException;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.utils.GUID;
import sos.model.interfaces.components.Manifest;
import sos.model.interfaces.policies.Policy;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manage the manifests of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManager {

    private SeaConfiguration configuration;
    private Policy policy;
    private MemCache cache;

    /**
     * Creates a manifests manager given a sea of stuff configuration object and
     * a policy for the sea of stuff. The configuration object is need to know the
     * locations for the manifests. The policy is required to know how and where
     * to store/retrieve the manifests.
     *
     * @param configuration
     * @param policy
     * @param cache
     */
    public ManifestsManager(SeaConfiguration configuration, Policy policy, MemCache cache) {
        this.configuration = configuration;
        this.policy = policy;
        this.cache = cache;
    }

    /**
     * Adds a manifest to the sea of stuff.
     * The current policy of the sea of stuff is used to determine where the manifest
     * is physically stored and how many copies are stored (replication).
     *
     * @param manifest
     */
    public void addManifest(Manifest manifest) throws ManifestSaveException {
        if (manifest.isValid()) {
            try {
                saveManifest(manifest);
            } catch (ManifestCacheException e) {
                throw new ManifestSaveException();
            } catch (ManifestPersistException e) {
                throw new ManifestSaveException();
            }
        }

        throw new ManifestSaveException("Manifest not valid");
    }

    /**
     * Find a manifest in the sea of stuff given a GUID.
     *
     * @param guid
     * @return
     */
    public Manifest findManifest(GUID guid) {

        // need to provide a general abstraction to what the redis cache (for example) does.
        // look at the sea of stuff interface to understand what we need.

        // Get manifest from file

        throw new NotImplementedException();
    }

    private void saveManifest(Manifest manifest) throws ManifestCacheException, ManifestPersistException {
        JSONObject manifestJSON = manifest.toJSON();

        // remove manifest guid and use that for the manifest file name
        String manifestGUID = manifestJSON.remove(ManifestConstants.KEY_MANIFEST_GUID).toString();
        saveToFileLocal(manifestGUID, manifestJSON); // TODO - local or remote based on policy

        cacheManifest(manifest);
    }

    private void saveToFileLocal(String filename, JSONObject object) throws ManifestPersistException {
        final String path = configuration.getLocalManifestsLocation() + filename;
        File file = new File(path);

        // if filepath doesn't exists, then create it
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            // TODO - custom exception
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);) {
            bufferedWriter.write(object.toString());
        } catch (IOException ioe) {
            throw new ManifestPersistException();
        } catch (Exception e) {
            throw new ManifestPersistException();
        } finally {
            file.setReadOnly();
        }

        // file already exists, thus there is no need to create another one.
    }

    private void cacheManifest(Manifest manifest) throws ManifestCacheException {
        try {
            cache.addManifest(manifest);
        } catch (UnknownManifestTypeException e) {
            throw new ManifestCacheException("Manifest could not be cached");
        }
    }
}
