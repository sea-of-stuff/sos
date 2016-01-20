package uk.ac.standrews.cs.sos.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.deserializers.AssetManifestDeserializer;
import uk.ac.standrews.cs.sos.deserializers.AtomManifestDeserializer;
import uk.ac.standrews.cs.sos.deserializers.CompoundManifestDeserializer;
import uk.ac.standrews.cs.sos.exceptions.*;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.*;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

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
        builder.registerTypeAdapter(CompoundManifest.class, new CompoundManifestDeserializer());
        builder.registerTypeAdapter(AssetManifest.class, new AssetManifestDeserializer());
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

        if (guid == null)
            throw new ManifestException();

        Manifest manifest;
        try {
             manifest = constructManifestFromCache(guid);
        } catch (UnknownManifestTypeException e) {
            try {
                manifest = getManifestFromFile(guid);
            } catch (UnknownGUIDException ex) {
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
        if (guid == null)
            throw new UnknownManifestTypeException();

        String type = cache.getManifestType(guid);

        if (type == null || type.isEmpty())
            throw new UnknownManifestTypeException();

        return constructManifestFromCache(guid, type);
    }

    private Manifest constructManifestFromCache(GUID guid, String type) throws UnknownManifestTypeException, MalformedURLException, ManifestNotMadeException {
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

    private Manifest getManifestFromFile(GUID guid) throws UnknownGUIDException {
        Manifest manifest;
        final String path = getManifestPath(guid);
        try {
            String manifestData = readManifestFromFile(path);

            JsonObject obj = gson.fromJson(manifestData, JsonObject.class);
            String type = obj.get(ManifestConstants.KEY_TYPE).getAsString();

            manifest = constructManifestFromJson(guid, type, manifestData);

        } catch (FileNotFoundException e) {
            throw new UnknownGUIDException();
        } catch (UnknownManifestTypeException e) {
            throw new UnknownGUIDException();
        }

        return manifest;
    }

    private Manifest constructManifestFromJson(GUID guid, String type, String manifestData) throws UnknownManifestTypeException {
        Manifest manifest = null;
        try {
            switch (type) {
                case ManifestConstants.ATOM:
                    manifest = gson.fromJson(manifestData, AtomManifest.class);
                    ((AtomManifest) manifest).setContentGUID(guid);
                    break;
                case ManifestConstants.COMPOUND:
                    manifest = gson.fromJson(manifestData, CompoundManifest.class);
                    break;
                case ManifestConstants.ASSET:
                    manifest = gson.fromJson(manifestData, AssetManifest.class);
                    break;
                default:
                    throw new UnknownManifestTypeException();
            }
        } catch (JsonSyntaxException e) {
            // TODO - throw exception
        }
        return manifest;

    }

    private String readManifestFromFile(String path) throws FileNotFoundException {
        // http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
        Scanner scanner = new Scanner(new File(path));
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        return text;
    }

    private void saveManifest(Manifest manifest) throws ManifestCacheException, ManifestPersistException {
        saveToFile(manifest);
        cacheManifest(manifest);
    }

    private void saveToFile(Manifest manifest) throws ManifestPersistException {
        JsonObject manifestJSON = manifest.toJSON();

        // Remove content guid and use that for the manifest file name
        String guid = "";
        String type = manifest.getManifestType();
        if (type.equals(ManifestConstants.ASSET)) {
            guid = manifestJSON.remove(ManifestConstants.KEY_VERSION).getAsString();
        } else {
            guid = manifestJSON.remove(ManifestConstants.KEY_CONTENT_GUID).getAsString();
        }

        final String path = getManifestPath(guid);
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
            String json = gson.toJson(manifestJSON);
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

    private String getManifestPath(GUID guid) {
        return configuration.getLocalManifestsLocation() + guid.toString();
    }

    private String getManifestPath(String guid) {
        return configuration.getLocalManifestsLocation() + guid;
    }
}
