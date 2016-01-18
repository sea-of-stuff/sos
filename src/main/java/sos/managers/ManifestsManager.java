package sos.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import sos.configurations.SeaConfiguration;
import sos.deserializers.AtomManifestDeserializer;
import sos.exceptions.*;
import sos.model.implementations.components.manifests.*;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.Location;
import sos.model.interfaces.components.Manifest;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Manage the manifests of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManager {

    private SeaConfiguration configuration;
    private MemCache cache;
    private Gson gson;

    /**
     * Creates a manifests manager given a sea of stuff configuration object and
     * a policy for the sea of stuff. The configuration object is need to know the
     * locations for the manifests.
     *
     * @param configuration
     * @param cache
     */
    public ManifestsManager(SeaConfiguration configuration, MemCache cache) {
        this.configuration = configuration;
        this.cache = cache;

        configureGson();
    }

    private void configureGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        registerGSonTypeAdapters(gsonBuilder);
        gson = gsonBuilder.create();
    }

    private void registerGSonTypeAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(AtomManifest.class, new AtomManifestDeserializer());
    }

    /**
     * Adds a manifest to the sea of stuff.
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
        // if query fails, then Look at Files then return
        // if this also fails, return null

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
        String type = cache.getManifestType(guid);
        return constructJsonObjectFromCache(guid, type);
    }

    private Manifest constructJsonObjectFromCache(GUID guid, String type) throws UnknownManifestTypeException, MalformedURLException, ManifestNotMadeException {
        Manifest manifest = null;

        switch (type) {
            case ManifestConstants.ATOM:
                manifest = constructAtomManifestFromCache(guid);
                break;
            case ManifestConstants.COMPOUND:
                manifest = constructCompoundManifestFromCache(guid);
                break;
            case ManifestConstants.ASSET:
                manifest = constructAssetManifestFromCache(guid);
                break;
            default:
                throw new UnknownManifestTypeException();
        }

        return manifest;
    }

    private AtomManifest constructAtomManifestFromCache(GUID guid) throws ManifestNotMadeException, MalformedURLException {
        Collection<Location> cachedLocations = cache.getLocations(guid);
        return ManifestFactory.createAtomManifest(guid, cachedLocations);
    }

    private CompoundManifest constructCompoundManifestFromCache(GUID guid) throws ManifestNotMadeException, MalformedURLException {
        String cachedSignature = cache.getSignature(guid);
        Collection<Content> cachedContents = cache.getContents(guid);
        return ManifestFactory.createCompoundManifest(cachedContents, cachedSignature);
    }

    private AssetManifest constructAssetManifestFromCache(GUID guid) throws ManifestNotMadeException, MalformedURLException {

        String cachedSignature = cache.getSignature(guid);

        Collection<Content> cachedContents = cache.getContents(guid);
        Iterator<Content> contentIterator = cachedContents.iterator();
        Content cachedContent = contentIterator.next();

        GUID cachedInvariant = cache.getInvariant(guid);
        Collection<GUID> cachedPrevs = cache.getPrevs(guid);
        Collection<GUID> cachedMetadata = cache.getMetadata(guid);

        return ManifestFactory.createAssetManifest(cachedContent, cachedInvariant, cachedPrevs, cachedMetadata, cachedSignature);
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
        throw new NotImplementedException();
    }

    private void saveManifest(Manifest manifest) throws ManifestCacheException, ManifestPersistException {
        JsonObject manifestJSON = manifest.toJSON();

        // Remove content guid and use that for the manifest file name
        String guid = "";
        String type = manifest.getManifestType();
        if (type.equals(ManifestConstants.ASSET)) {
            guid = manifestJSON.remove(ManifestConstants.KEY_VERSION).getAsString();
        } else {
            guid = manifestJSON.remove(ManifestConstants.KEY_CONTENT_GUID).getAsString();
        }
        saveToFile(guid, manifestJSON);

        cacheManifest(manifest);
    }

    private void saveToFile(String filename, JsonObject object) throws ManifestPersistException {
        final String path = configuration.getLocalManifestsLocation() + filename;
        File file = new File(path);

        // if filepath doesn't exists, then create it
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            // TODO - custom exception
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        if (file.exists())
            return;

        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
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
    }

    private void cacheManifest(Manifest manifest) throws ManifestCacheException {
        try {
            cache.addManifest(manifest);
        } catch (UnknownManifestTypeException e) {
            throw new ManifestCacheException("Manifest could not be cached");
        }
    }
}
