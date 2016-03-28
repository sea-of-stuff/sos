package uk.ac.standrews.cs.sos.model.manifests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import uk.ac.standrews.cs.sos.deserializers.AssetManifestDeserializer;
import uk.ac.standrews.cs.sos.deserializers.AtomManifestDeserializer;
import uk.ac.standrews.cs.sos.deserializers.CompoundManifestDeserializer;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestMergeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestCacheException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.UnknownGUIDException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.utils.FileHelper;
import uk.ac.standrews.cs.utils.GUID;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Manage the manifests of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManager {

    private final static String BACKUP_EXTENSION = ".bak";
    private final static String JSON_EXTENSION = ".json";
    
    private static final int DEFAULT_RESULTS = 10;
    private static final int DEFAULT_SKIP_RESULTS = 0;

    final private SeaConfiguration configuration;
    final private Index index;
    private Gson gson;

    /**
     * Creates a manifests manager given a sea of stuff configuration object and
     * a policy for the sea of stuff. The configuration object is need to know the
     * locations for the manifests.
     *
     * @param configuration used by the manifest manager
     * @param index used to record information for the manifests.
     */
    public ManifestsManager(SeaConfiguration configuration, Index index) {
        this.configuration = configuration;
        this.index = index;

        configureGson();
    }

    /**
     * Adds a manifest to the sea of stuff.
     *
     * @param manifest to be added to the sea of stuff
     */
    public void addManifest(Manifest manifest) throws ManifestPersistException {
        if (manifest.isValid()) {
            try {
                saveManifest(manifest);
            } catch (ManifestCacheException e) {
                throw new ManifestPersistException("Manifest could not be cached");
            } catch (ManifestPersistException e) {
                throw new ManifestPersistException("Manifest could not be persisted");
            } catch (UnknownGUIDException | ManifestMergeException e) {
                throw new ManifestPersistException("An equivalent manifest exists, but could not be fetched or merged");
            }
        } else {
            throw new ManifestPersistException("Manifest not valid");
        }
    }

    /**
     * Find a manifest in the sea of stuff given a GUID.
     *
     * @param guid of the manifest to be found
     * @return Manifest
     * @throws ManifestNotFoundException
     */
    public Manifest findManifest(GUID guid) throws ManifestNotFoundException {
        if (guid == null) {
            throw new ManifestNotFoundException();
        }

        Manifest manifest;
        try {
            manifest = getManifestFromFile(guid);
        } catch (UnknownGUIDException ex) {
            throw new ManifestNotFoundException();
        }

        return manifest;
    }

    public Collection<GUID> findManifestsByType(String type) throws ManifestNotFoundException {
        Collection<GUID> retval;
        try {
            retval = index.getManifestsOfType(type, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        } catch (IndexException e) {
            throw new ManifestNotFoundException();
        }
        return retval;
    }

    public Collection<GUID> findVersions(GUID guid) throws ManifestNotFoundException {
        Collection<GUID> retval;
        try {
            retval = index.getVersions(guid, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        } catch (IndexException e) {
            throw new ManifestNotFoundException();
        }
        return retval;
    }

    public Collection<GUID> findManifestsThatMatchLabel(String label) throws ManifestNotFoundException {
        Collection<GUID> retval;
        try {
            retval = index.getMetaLabelMatches(label, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        } catch (IndexException e) {
            throw new ManifestNotFoundException();
        }
        return retval;
    }

    /**************************************************************************/

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

    private Manifest getManifestFromFile(GUID guid) throws UnknownGUIDException {
        Manifest manifest;
        final String path = getManifestPath(guid);
        try {
            String manifestData = readManifestFromFile(path);

            JsonObject obj = gson.fromJson(manifestData, JsonObject.class);
            String type = obj.get(ManifestConstants.KEY_TYPE).getAsString();

            manifest = constructManifestFromJson(type, manifestData);
        } catch (FileNotFoundException | UnknownManifestTypeException | ManifestNotMadeException e) {
            throw new UnknownGUIDException();
        }

        return manifest;
    }

    private String readManifestFromFile(String path) throws FileNotFoundException {
        // http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
        String text;
        try (Scanner scanner = new Scanner(new File(path))) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }

    private Manifest constructManifestFromJson(String type, String manifestData) throws UnknownManifestTypeException, ManifestNotMadeException {
        Manifest manifest;
        try {
            switch (type) {
                case ManifestConstants.ATOM:
                    manifest = gson.fromJson(manifestData, AtomManifest.class);
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
            throw new ManifestNotMadeException();
        }
        return manifest;
    }

    // if atom-manifest, check if it exists already
    // then merge and save
    // otherwise just save
    private void saveManifest(Manifest manifest) throws ManifestCacheException, ManifestPersistException, UnknownGUIDException, ManifestMergeException {
        if (manifest.getManifestType().equals(ManifestConstants.ATOM) &&
                manifestExistsInLocalStorage(manifest.getContentGUID())) {
            mergeAtomManifestAndSave(manifest);
        } else {
            saveToFile(manifest);
        }

        cacheManifest(manifest);
    }

    private void mergeAtomManifestAndSave(Manifest manifest) throws ManifestPersistException, UnknownGUIDException, ManifestMergeException{
        GUID guid = manifest.getContentGUID();
        Manifest existingManifest = getManifestFromFile(guid);
        String backupPath = backupManifest(existingManifest);

        if (!existingManifest.equals(manifest)) {
            manifest = mergeManifests(guid, (AtomManifest) existingManifest, (AtomManifest) manifest);
            FileHelper.deleteFile(backupPath);
            saveToFile(manifest);
        }

        FileHelper.deleteFile(backupPath + BACKUP_EXTENSION);
    }

    private String backupManifest(Manifest manifest) throws ManifestMergeException {
        GUID guidUsedToStoreManifest = getGUIDUsedToStoreManifest(manifest);
        String originalPath = getManifestPath(guidUsedToStoreManifest);
        try {
            FileHelper.copyToFile(new URILocation(originalPath).getSource(),
                    originalPath + BACKUP_EXTENSION);
        } catch (IOException | URISyntaxException e) {
            throw new ManifestMergeException();
        }
        return originalPath;
    }

    private GUID getGUIDUsedToStoreManifest(Manifest manifest) {
        GUID guid;
        if (manifest.getManifestType().equals(ManifestConstants.ASSET)) {
            guid = ((AssetManifest) manifest).getVersionGUID();
        } else {
            guid = manifest.getContentGUID();
        }
        return guid;
    }

    private void saveToFile(Manifest manifest) throws ManifestPersistException {
        JsonObject manifestJSON = manifest.toJSON();

        String guid = getGUIDForManifestFilename(manifest);
        final String path = getManifestPath(guid);
        File file = new File(path);

        // if filepath doesn't exists, then create it
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            throw new ManifestPersistException();
        }

        if (file.exists())
            return;

        writeToFile(file, manifestJSON);
    }

    private String getGUIDForManifestFilename(Manifest manifest) {
        JsonObject manifestJSON = manifest.toJSON();

        String guid;
        String type = manifest.getManifestType();
        if (type.equals(ManifestConstants.ASSET)) {
            guid = manifestJSON.get(ManifestConstants.KEY_VERSION).getAsString();
        } else {
            guid = manifestJSON.get(ManifestConstants.KEY_CONTENT_GUID).getAsString();
        }
        return guid;
    }

    private void writeToFile(File file, JsonObject manifest) throws ManifestPersistException {
        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            Gson gson = new Gson();
            String json = gson.toJson(manifest);
            bufferedWriter.write(json);
        } catch (Exception e) {
            throw new ManifestPersistException();
        } finally {
            file.setReadOnly();
        }
    }

    private void cacheManifest(Manifest manifest) throws ManifestCacheException {
        try {
            index.addManifest(manifest);
        } catch (IndexException e) {
            throw new ManifestCacheException("Manifest could not be cached");
        }
    }

    private String getManifestPath(GUID guid) {
        return getManifestPath(guid.toString());
    }

    private String getManifestPath(String guid) {
        return configuration.getLocalManifestsLocation() + normaliseGUID(guid);
    }

    private String normaliseGUID(String guid) {
        return guid + JSON_EXTENSION;
    }

    private Manifest mergeManifests(GUID guid, AtomManifest first, AtomManifest second) {
        HashSet<LocationBundle> locations = new HashSet<>();
        locations.addAll(first.getLocations());
        locations.addAll(second.getLocations());

        return ManifestFactory.createAtomManifest(guid, locations);
    }

    private boolean manifestExistsInLocalStorage(GUID guid) {
        String path = getManifestPath(guid);
        return new File(path).exists();
    }

}
