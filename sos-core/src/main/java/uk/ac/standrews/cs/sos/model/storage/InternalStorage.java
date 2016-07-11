package uk.ac.standrews.cs.sos.model.storage;

import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;
import uk.ac.standrews.cs.storage.interfaces.IStorage;

/**
 * This is a SOS specific wrapper on the IStorage module.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class InternalStorage {

    public static final String DATA_DIRECTORY_NAME = "data";
    public static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    public static final String INDEX_DIRECTORY_NAME = "index";

    private IStorage storage;

    public InternalStorage(IStorage storage) throws DataStorageException {
        this.storage = storage;

        createSOSDirectories();
    }

    public Directory getDataDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(DATA_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    public Directory getManifestDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(MANIFESTS_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    public Directory getIndexDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(INDEX_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    public File createFile(Directory parent, String filename) throws DataStorageException {
        try {
            return storage.createFile(parent, filename);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    public File createFile(Directory parent, String filename, Data data) throws DataStorageException {
        try {
            return storage.createFile(parent, filename, data);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    public void destroy() throws DataStorageException {

        try {
            storage.getRoot().remove(DATA_DIRECTORY_NAME);
            storage.getRoot().remove(MANIFESTS_DIRECTORY_NAME);
            storage.getRoot().remove(INDEX_DIRECTORY_NAME);
        } catch (BindingAbsentException e) {
            throw new DataStorageException(e);
        }
    }

    protected void createSOSDirectories() throws DataStorageException {
        try {
            storage.createDirectory(DATA_DIRECTORY_NAME).persist();
            storage.createDirectory(MANIFESTS_DIRECTORY_NAME).persist();
            storage.createDirectory(INDEX_DIRECTORY_NAME).persist();
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }
}
