package uk.ac.standrews.cs.sos.utils;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsDirectoryException;
import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.usro.RoleImpl;
import uk.ac.standrews.cs.sos.impl.usro.UserImpl;
import uk.ac.standrews.cs.sos.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileUtils {

    public static final String JSON_EXTENSION = ".json";

    public static Manifest ManifestFromFile(IFile file) throws ManifestNotFoundException {

        try {
            JsonNode node = JSONHelper.jsonObjMapper().readTree(file.toFile());
            ManifestType type = ManifestType.get(node.get(JSONConstants.KEY_TYPE).textValue());

            return constructManifestFromJsonFile(type, file);
        } catch (UnknownManifestTypeException | ManifestNotMadeException | IOException e) {
            throw new ManifestNotFoundException("Unable to find manifest given file " + file.getPathname(), e);
        }
    }

    public static Manifest ManifestFromJson(String json) throws ManifestNotFoundException {

        try {
            JsonNode node = JSONHelper.jsonObjMapper().readTree(json);
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
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), Atom.class);
                    break;
                case ATOM_PROTECTED:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), SecureAtom.class);
                    break;
                case COMPOUND:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), Compound.class);
                    break;
                case COMPOUND_PROTECTED:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), SecureCompound.class);
                    break;
                case VERSION:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), Version.class);
                    break;

                case METADATA:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), Metadata.class);
                    break;
                case METADATA_PROTECTED:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), SecureMetadata.class);
                    break;

                case CONTEXT:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), Context.class);
                    break;
                case PREDICATE:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), Predicate.class);
                    break;
                case POLICY:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), Policy.class);
                    break;

                case USER:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), User.class);
                    break;
                    case ROLE:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), Role.class);
                    break;

                case NODE:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData.toFile(), Node.class);
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
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, Atom.class);
                    break;
                case ATOM_PROTECTED:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, SecureAtom.class);
                    break;
                case COMPOUND:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, Compound.class);
                    break;
                case COMPOUND_PROTECTED:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, SecureCompound.class);
                    break;
                case VERSION:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, Version.class);
                    break;

                case METADATA:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, Metadata.class);
                    break;
                case METADATA_PROTECTED:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, SecureMetadata.class);
                    break;

                case CONTEXT:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, Context.class);
                    break;
                case PREDICATE:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, Predicate.class);
                    break;
                case POLICY:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, Policy.class);
                    break;

                case USER:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, User.class);
                    break;
                case ROLE:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, Role.class);
                    break;

                case NODE:
                    manifest = JSONHelper.jsonObjMapper().readValue(manifestData, Node.class);
                    break;

                default:
                    throw new UnknownManifestTypeException("Manifest type " + type + " is unknown");
            }
        } catch (IOException e) {
            throw new ManifestNotMadeException("Unable to create a manifest from json " + manifestData);
        }

        return manifest;
    }

    public static User UserFromFile(File file) throws UserNotFoundException {

        try {
            return JSONHelper.jsonObjMapper().readValue(file, UserImpl.class);

        } catch (IOException e) {
            throw new UserNotFoundException();
        }
    }

    public static User UserFromFile(IFile file) throws UserNotFoundException {

        try {
            return UserFromFile(file.toFile());

        } catch (IOException e) {
            throw new UserNotFoundException();
        }
    }

    public static User UserFromString(String json) throws UserNotFoundException {

        try {
            return JSONHelper.jsonObjMapper().readValue(json, UserImpl.class);

        } catch (IOException e) {
            throw new UserNotFoundException();
        }
    }

    public static Role RoleFromFile(IFile file) throws RoleNotFoundException {

        try {
            return JSONHelper.jsonObjMapper().readValue(file.toFile(), RoleImpl.class);

        } catch (IOException e) {
            throw new RoleNotFoundException();
        }
    }


    public static Role RoleFromString(String json) throws RoleNotFoundException {

        try {
            return JSONHelper.jsonObjMapper().readValue(json, RoleImpl.class);

        } catch (IOException e) {
            throw new RoleNotFoundException();
        }
    }

    /**
     * Create a file object.
     * The file object must be persisted by the caller.
     *
     * @param storage
     * @param directory
     * @param filename
     * @return
     * @throws DataStorageException
     */
    public static IFile CreateFile(LocalStorage storage, IDirectory directory, String filename) throws DataStorageException {
        return storage.createFile(directory, filename);
    }

    /**
     * Create a file object.
     * The file object must be persisted by the caller.
     *
     * @param storage
     * @param directory
     * @param filename
     * @return
     * @throws DataStorageException
     */
    public static IFile CreateFile(LocalStorage storage, IDirectory directory, String filename, String extension) throws DataStorageException {
        return CreateFile(storage, directory, filename + extension);
    }

    /**
     * Create a file object with some given string content.
     * The file object must be persisted by the caller.
     */
    public static IFile CreateFileWithContent(LocalStorage storage, IDirectory directory, String filename, String content) throws DataStorageException {
        return storage.createFile(directory, filename, new StringData(content));
    }

    public static IFile CreateTempFile(LocalStorage storage, IDirectory directory, String guid) throws DataStorageException {
        return storage.createFile(directory, guid + "-TEMP");
    }

    public static String FileContent(LocalStorage storage, IDirectory directory, String filename) throws DataStorageException, DataException {

        try (InputStream inputStream = CreateFile(storage, directory, filename).getData().getInputStream()) {

            return IO.InputStreamToString(inputStream);
        } catch (IOException e) {
            throw new DataException("Unable to get data");
        }
    }

    public static void DeleteFile(IFile file) throws ManifestsDirectoryException {
        IDirectory parent = file.getParent();
        try {
            parent.remove(file.getName());
        } catch (BindingAbsentException e) {
            throw new ManifestsDirectoryException("Unable to delete file " + file.getName(), e);
        }
    }

    public static void MakePath(String path) {
        java.io.File file = new java.io.File(path);
        java.io.File parent = file.getParentFile();
        if (parent != null)
            parent.mkdirs();

        if (path.endsWith("/")) {
            file.mkdir();
        }
    }

}
