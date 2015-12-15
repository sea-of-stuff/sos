package sos.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import sos.configurations.SeaConfiguration;
import sos.exceptions.*;
import sos.model.implementations.components.manifests.AssetManifest;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.components.manifests.CompoundManifest;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.utils.GUID;
import sos.model.interfaces.components.Manifest;
import sos.model.interfaces.policies.Policy;

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


    // need to provide a general abstraction to what the redis cache (for example) does.
    // look at the sea of stuff interface to understand what we need.
    // Get manifest from file
    // https://github.com/google/gson/blob/master/UserGuide.md#object-examples
    /**
     * Find a manifest in the sea of stuff given a GUID.
     *
     * @param guid
     * @return
     * @throws ManifestException
     */
    public Manifest findManifest(GUID guid) throws ManifestException {
        // Look at REDIS, then return
        // if fails, then Look at Files then return
        // if fails, return null

        Manifest manifest;
        try {
             manifest = constructManifestFromCache(guid);
        } catch (UnknownManifestTypeException e) {
            try {
                manifest = getManifestFromFile(guid);
            } catch (SourceLocationException ex) {
                throw new ManifestException();
            }
        }

        return manifest;
    }

    private Manifest constructManifestFromCache(GUID guid) throws UnknownManifestTypeException {
        // Retrieve all useful information from redis/cache and build a manifest

        String type = cache.getManifestType(guid);
        JsonObject obj = constructJsonObjectFromCache(guid, type);

        return constructManifestFromJson(type, obj);
    }

    private JsonObject constructJsonObjectFromCache(GUID guid, String type) throws UnknownManifestTypeException {
        JsonObject obj = null;

        switch (type) {
            case ManifestConstants.ATOM:
                obj = constructAtomJsonObjectFromCache(guid, type);
                break;
            case ManifestConstants.COMPOUND:

                break;
            case ManifestConstants.ASSET:

                break;
            default:
                throw new UnknownManifestTypeException();
        }

        return obj;
    }

    private JsonObject constructAtomJsonObjectFromCache(GUID guid, String type) {
        JsonObject obj = new JsonObject();




        return obj;
    }

    private Manifest constructManifestFromJson(String type, JsonObject obj) throws UnknownManifestTypeException {
        Manifest manifest = null;
        Gson gson = new Gson();
        try {
            switch (type) {
                case ManifestConstants.ATOM:
                    manifest = gson.fromJson(obj, AtomManifest.class);
                    break;
                case ManifestConstants.COMPOUND:
                    manifest = gson.fromJson(obj, CompoundManifest.class);
                    break;
                case ManifestConstants.ASSET:
                    manifest = gson.fromJson(obj, AssetManifest.class);
                    break;
                default:
                    throw new UnknownManifestTypeException();
            }
        } catch (JsonSyntaxException e) {
            // TODO - throw exception
        }
        return manifest;

    }

    private Manifest getManifestFromFile(GUID guid) throws SourceLocationException {
        return null;
    }

    // NOTE - local or remote based on policy
    private void saveManifest(Manifest manifest) throws ManifestCacheException, ManifestPersistException {
        JsonObject manifestJSON = manifest.toJSON();

        // remove manifest guid and use that for the manifest file name
        String manifestGUID = manifestJSON.remove(ManifestConstants.KEY_MANIFEST_GUID).toString();
        saveToFileLocal(manifestGUID, manifestJSON);

        cacheManifest(manifest);
    }

    private void saveToFileLocal(String filename, JsonObject object) throws ManifestPersistException {
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
            Gson gson = new Gson();
            bufferedWriter.write(gson.toJson(object));
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
