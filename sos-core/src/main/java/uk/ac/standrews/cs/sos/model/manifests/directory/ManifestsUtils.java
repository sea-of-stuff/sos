package uk.ac.standrews.cs.sos.model.manifests.directory;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsUtils {

    private final static String JSON_EXTENSION = ".json";

    public static Manifest ManifestFromFile(File file) throws ManifestNotFoundException {

        try {
            JsonNode node = JSONHelper.JsonObjMapper().readTree(file.toFile());
            ManifestType type = ManifestType.get(node.get(ManifestConstants.KEY_TYPE).textValue());

            return constructManifestFromJson(type, file);
        } catch (UnknownManifestTypeException | ManifestNotMadeException | IOException e) {
            throw new ManifestNotFoundException("Unable to find manifest given file " + file.getPathname(), e);
        }

    }

    public static Manifest ManifestFromJson(String json) throws ManifestNotFoundException {

        try {
            JsonNode node = JSONHelper.JsonObjMapper().readTree(json);
            ManifestType type = ManifestType.get(node.get(ManifestConstants.KEY_TYPE).textValue());

            return constructManifestFromJson(type, json);
        } catch (UnknownManifestTypeException | ManifestNotMadeException | IOException e) {
            throw new ManifestNotFoundException("Unable to construct manifest from JSON" + json, e);
        }

    }

    private static Manifest constructManifestFromJson(ManifestType type, File manifestData) throws UnknownManifestTypeException, ManifestNotMadeException {
        Manifest manifest;
        try {
            switch (type) {
                case ATOM:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), AtomManifest.class);
                    break;
                case COMPOUND:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), CompoundManifest.class);
                    break;
                case ASSET:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), AssetManifest.class);
                    break;
                default:
                    throw new UnknownManifestTypeException("Manifest type " + type + " is unknown");
            }
        } catch (IOException e) {
            throw new ManifestNotMadeException("Unable to create a manifest from file at " + manifestData.getPathname());
        }

        return manifest;
    }

    private static Manifest constructManifestFromJson(ManifestType type, String manifestData) throws UnknownManifestTypeException, ManifestNotMadeException {
        Manifest manifest;
        try {
            switch (type) {
                case ATOM:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData, AtomManifest.class);
                    break;
                case COMPOUND:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData, CompoundManifest.class);
                    break;
                case ASSET:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData, AssetManifest.class);
                    break;
                default:
                    throw new UnknownManifestTypeException("Manifest type " + type + " is unknown");
            }
        } catch (IOException e) {
            throw new ManifestNotMadeException("Unable to create a manifest from json " + manifestData);
        }

        return manifest;
    }

    public static File ManifestFile(LocalStorage storage, Directory directory, String guid) throws DataStorageException {
        return storage.createFile(directory, normaliseGUID(guid));
    }

    public static File ManifestTempFile(LocalStorage storage, Directory directory, String guid) throws DataStorageException {
        return storage.createFile(directory, normaliseGUID(guid) + "-TEMP");
    }

    private static String normaliseGUID(String guid) {
        return guid + JSON_EXTENSION;
    }

}
