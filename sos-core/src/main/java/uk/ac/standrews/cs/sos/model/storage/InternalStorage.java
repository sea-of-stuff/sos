package uk.ac.standrews.cs.sos.model.storage;

import uk.ac.standrews.cs.sos.exceptions.DataStorageException;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;
import uk.ac.standrews.cs.storage.interfaces.IStorage;

import java.io.IOException;

/**
 * This is a SOS specific wrapper on the IStorage module.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class InternalStorage {

    public static final String DATA_DIRECTORY_NAME = "data";
    public static final String MANIFESTS_DIRECTORY_NAME = "manifests";

    private IStorage storage;

    public InternalStorage(IStorage storage) throws DataStorageException {
        this.storage = storage;

        try {
            createSOSDirectories();
        } catch (PersistenceException | IOException e) {
            throw new DataStorageException(e);
        }
    }

    public Directory getDataDirectory() throws IOException {
        return storage.createDirectory(DATA_DIRECTORY_NAME);
    }

    public Directory getManifestDirectory() throws IOException {
        return storage.createDirectory(MANIFESTS_DIRECTORY_NAME);
    }

    public Directory createDirectory(Directory parent, String name) throws IOException {
        return storage.createDirectory(parent, name);
    }

    public Directory createDirectory(String name) throws IOException {
        return storage.createDirectory(name);
    }

    public File createFile(Directory parent, String filename) throws IOException {
        return storage.createFile(parent, filename);
    }

    public File createFile(Directory parent, String filename, Data data) throws IOException {
        return storage.createFile(parent, filename, data);
    }

    public void destroy() throws DataStorageException {

        try {
            storage.getRoot().remove(DATA_DIRECTORY_NAME);
            storage.getRoot().remove(MANIFESTS_DIRECTORY_NAME);
        } catch (BindingAbsentException e) {
            throw new DataStorageException(e);
        }
    }

    protected void createSOSDirectories() throws PersistenceException, IOException {
        storage.createDirectory(DATA_DIRECTORY_NAME).persist();
        storage.createDirectory(MANIFESTS_DIRECTORY_NAME).persist();
    }
}
