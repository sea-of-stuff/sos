package model.managers;

import configurations.SeaConfiguration;
import model.implementations.components.manifests.ManifestConstants;
import model.implementations.utils.GUID;
import model.interfaces.components.Manifest;
import model.interfaces.policies.Policy;
import org.json.JSONObject;
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

    /**
     * Creates a manifests manager given a sea of stuff configuration object and
     * a policy for the sea of stuff. The configuration object is need to know the
     * locations for the manifests. The policy is required to know how and where
     * to store/retrieve the manifests.
     *
     * @param configuration
     * @param policy
     */
    public ManifestsManager(SeaConfiguration configuration, Policy policy) {
        this.configuration = configuration;
        this.policy = policy;
    }

    /**
     * Adds a manifest to the sea of stuff.
     * The current policy of the sea of stuff is used to determine where the manifest
     * is physically stored and how many copies are stored (replication).
     *
     * @param manifest
     */
    // TODO - throw exception if manifest could not be added.
    public void addManifest(Manifest manifest) {
        if (manifest.isValid()) {
            saveManifest(manifest);
        }
    }

    /**
     * Find a manifest in the sea of stuff given a GUID.
     *
     * @param guid
     * @return
     */
    public Manifest findManifest(GUID guid) {
        // TODO - maybe provide other similar methods.
        throw new NotImplementedException();
    }

    private void cacheManifest(Manifest manifest) {
        // TODO - cache for fast retrieval, redis (not really for local use, but might be useful if this node has to server incoming requests)
        // sqlite - easy to use, reasonably fast
    }

    private void saveManifest(Manifest manifest) {
        JSONObject manifestJSON = manifest.toJSON();
        // TODO - remove manifest guid and use that for the manifest file name

        String manifestGUID = manifestJSON.remove(ManifestConstants.KEY_MANIFEST_GUID).toString();

        // TODO - local or remote based on policy
        // save to file
        saveToFileLocal(manifestGUID, manifestJSON);
    }

    private void saveToFileLocal(String filename, JSONObject object) {
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
            // TODO - throw new exception
        } catch (Exception e) {

        } finally {
            file.setReadOnly();
        }

        // file already exists, thus there is no need to create another one.
    }
}
