package sos.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import sos.configurations.SeaConfiguration;
import sos.exceptions.*;
import sos.model.implementations.components.manifests.*;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.implementations.utils.Location;
import sos.model.implementations.utils.URLLocation;
import sos.model.interfaces.components.Manifest;
import sos.model.interfaces.policies.Policy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

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
        } else {
            throw new ManifestSaveException("Manifest not valid");
        }
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
        } catch (MalformedURLException e) {
            throw new ManifestException();
        } catch (ManifestNotMadeException e) {
            throw new ManifestException();
        }

        return manifest;
    }

    private Manifest constructManifestFromCache(GUID guid) throws UnknownManifestTypeException, MalformedURLException, ManifestNotMadeException {
        // Retrieve all useful information from redis/cache and build a manifest

        String type = cache.getManifestType(guid);

        return constructJsonObjectFromCache(guid, type);
    }

    private Manifest constructJsonObjectFromCache(GUID guid, String type) throws UnknownManifestTypeException, MalformedURLException, ManifestNotMadeException {
        Manifest manifest = null;

        switch (type) {
            case ManifestConstants.ATOM:
                manifest = constructAtomManifestFromCache(guid, type);
                break;
            case ManifestConstants.COMPOUND:

                break;
            case ManifestConstants.ASSET:

                break;
            default:
                throw new UnknownManifestTypeException();
        }

        return manifest;
    }

    private AtomManifest constructAtomManifestFromCache(GUID guid, String type) throws ManifestNotMadeException, MalformedURLException {

        GUID contentGUID = new GUIDsha1(cache.getContent(guid));
        Collection<String> cachedLocations = cache.getLocations(guid);
        Collection<Location> locations = new ArrayList<Location>();
        for(String cachedLocation:cachedLocations) {
            locations.add(new URLLocation(cachedLocation));
        }

        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, contentGUID, locations);

        return manifest;
    }

    private CompoundManifest constructCompoundManifestFromCache(GUID guid, String type) throws ManifestNotMadeException, MalformedURLException {

        /*GUID contentGUID = new GUID(cache.getContent(guid));
        Collection<String> cachedLocations = cache.getLocations(guid);
        Collection<Location> locations = new ArrayList<Location>();
        for(String cachedLocation:cachedLocations) {
            locations.add(new URLLocation(cachedLocation));
        }

        CompoundManifest manifest = ManifestFactory.createAtomManifest(guid, contentGUID, locations);

        return manifest;*/
        return null;
    }

    private AssetManifest constructAssetManifestFromCache(GUID guid, String type) throws ManifestNotMadeException, MalformedURLException {

        /*GUID contentGUID = new GUID(cache.getContent(guid));
        Collection<String> cachedLocations = cache.getLocations(guid);
        Collection<Location> locations = new ArrayList<Location>();
        for(String cachedLocation:cachedLocations) {
            locations.add(new URLLocation(cachedLocation));
        }

        AssetManifest manifest = ManifestFactory.createAtomManifest(guid, contentGUID, locations);

        return manifest;*/
        return null;
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
        String manifestGUID = manifestJSON.remove(ManifestConstants.KEY_MANIFEST_GUID).getAsString();
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
            String json = gson.toJson(object);
            bufferedWriter.write(json);
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
