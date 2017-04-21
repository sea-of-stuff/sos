package uk.ac.standrews.cs.sos.impl.storage;

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
public class LocalStorage {

    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    private static final String NODE_DIRECTORY_NAME = "node"; // where all internal data structures and setting files are stored

    // The actual storage used by this node
    private IStorage storage;

    /**
     * Construct the storage space used by this node.
     *
     * @param storage
     * @throws DataStorageException
     */
    public LocalStorage(IStorage storage) throws DataStorageException {
        this.storage = storage;

        createSOSDirectories();
    }

    /**
     * Return the directory used to store data
     * @return data directory
     * @throws DataStorageException
     */
    public Directory getDataDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(DATA_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Return the directory used to store the manifests
     * @return manifest directory
     * @throws DataStorageException
     */
    public Directory getManifestsDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(MANIFESTS_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Return the directory used to store the caches
     * @return caches directory
     * @throws DataStorageException
     */
    public Directory getNodeDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(NODE_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Create an arbitrary file in a given directory
     * @param parent
     * @param filename
     * @return file being created
     * @throws DataStorageException
     */
    public File createFile(Directory parent, String filename) throws DataStorageException {
        try {
            return storage.createFile(parent, filename);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Create an arbitrary file in a given directory and some data
     * @param parent
     * @param filename
     * @param data
     * @return file being created
     * @throws DataStorageException
     */
    public File createFile(Directory parent, String filename, Data data) throws DataStorageException {
        try {
            return storage.createFile(parent, filename, data);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Destroy the internal storage and its content.
     * This method should be used for test purposes only.
     *
     * @throws DataStorageException
     */
    public void destroy() throws DataStorageException {

        try {
            remove(DATA_DIRECTORY_NAME);
            remove(MANIFESTS_DIRECTORY_NAME);
            remove(NODE_DIRECTORY_NAME);
        } catch (BindingAbsentException e) {
            throw new DataStorageException(e);
        }
    }

    private void remove(String directoryName) throws BindingAbsentException {
        if (storage.getRoot().contains(directoryName)) {
            storage.getRoot().remove(directoryName);
        }
    }

    private void createSOSDirectories() throws DataStorageException {
        try {
            storage.createDirectory(DATA_DIRECTORY_NAME).persist();
            storage.createDirectory(MANIFESTS_DIRECTORY_NAME).persist();
            storage.createDirectory(NODE_DIRECTORY_NAME).persist();
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }
}
