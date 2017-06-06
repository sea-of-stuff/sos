package uk.ac.standrews.cs.sos.impl.manifests.directory;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.impl.manifests.VersionManifest;
import uk.ac.standrews.cs.sos.impl.metadata.basic.BasicMetadata;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.roles.RoleImpl;
import uk.ac.standrews.cs.sos.impl.roles.UserImpl;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileUtils {

    public final static String JSON_EXTENSION = ".json";

    public static Manifest ManifestFromFile(IFile file) throws ManifestNotFoundException {

        try {
            JsonNode node = JSONHelper.JsonObjMapper().readTree(file.toFile());
            ManifestType type = ManifestType.get(node.get(JSONConstants.KEY_TYPE).textValue());

            return constructManifestFromJsonFile(type, file);
        } catch (UnknownManifestTypeException | ManifestNotMadeException | IOException e) {
            throw new ManifestNotFoundException("Unable to find manifest given file " + file.getPathname(), e);
        }
    }

    public static Manifest ManifestFromJson(String json) throws ManifestNotFoundException {

        try {
            JsonNode node = JSONHelper.JsonObjMapper().readTree(json);
            ManifestType type = ManifestType.get(node.get(JSONConstants.KEY_TYPE).textValue());

            return constructManifestFromJson(type, json);
        } catch (UnknownManifestTypeException | ManifestNotMadeException | IOException e) {
            throw new ManifestNotFoundException("Unable to construct manifest from JSON" + json, e);
        }
    }

    private static Manifest constructManifestFromJsonFile(ManifestType type, IFile manifestData) throws UnknownManifestTypeException, ManifestNotMadeException {
        Manifest manifest;
        try {
            switch (type) {
                case ATOM:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), AtomManifest.class);
                    break;
                case COMPOUND:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), CompoundManifest.class);
                    break;
                case VERSION:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), VersionManifest.class);
                    break;
                case METADATA:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), BasicMetadata.class);
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
                case VERSION:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData, VersionManifest.class);
                    break;
                case METADATA:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData, BasicMetadata.class);
                    break;
                default:
                    throw new UnknownManifestTypeException("Manifest type " + type + " is unknown");
            }
        } catch (IOException e) {
            throw new ManifestNotMadeException("Unable to create a manifest from json " + manifestData);
        }

        return manifest;
    }

    public static User UserFromFile(IFile file) throws UserNotFoundException {

        try {
            return JSONHelper.JsonObjMapper().readValue(file.toFile(), UserImpl.class);

        } catch (IOException e) {
            throw new UserNotFoundException();
        }
    }

    public static Role RoleFromFile(IFile file) throws RoleNotFoundException {

        try {
            return JSONHelper.JsonObjMapper().readValue(file.toFile(), RoleImpl.class);

        } catch (IOException e) {
            throw new RoleNotFoundException();
        }
    }

    public static IFile File(LocalStorage storage, IDirectory directory, String filename) throws DataStorageException {
        return storage.createFile(directory, filename);
    }

    public static IFile File(LocalStorage storage, IDirectory directory, String filename, String extension) throws DataStorageException {
        return File(storage, directory, filename + extension);
    }

    public static IFile TempFile(LocalStorage storage, IDirectory directory, String guid) throws DataStorageException {
        return storage.createFile(directory, guid + "-TEMP");
    }

}
