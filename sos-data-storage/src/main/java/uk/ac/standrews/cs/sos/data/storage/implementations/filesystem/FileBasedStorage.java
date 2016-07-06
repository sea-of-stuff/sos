package uk.ac.standrews.cs.sos.storage.implementations.filesystem;


import org.apache.commons.io.FileUtils;
import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.exceptions.DestroyException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.exceptions.StorageException;
import uk.ac.standrews.cs.sos.storage.implementations.CommonStorage;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedStorage extends CommonStorage implements Storage {

    private Directory root;
    private boolean isImmutable;

    public FileBasedStorage(java.io.File rootDirectory, boolean isImmutable) throws StorageException {
        super(isImmutable);

        root = new FileBasedDirectory(rootDirectory);
        try {
            root.persist();
            createSOSDirectories();
        } catch (PersistenceException | IOException e) {
            throw new StorageException(e);
        }
        this.isImmutable = isImmutable;
    }

    @Override
    public boolean isImmutable() {
        return isImmutable;
    }

    @Override
    public Directory getRoot() {
        return root;
    }


    @Override
    public Directory createDirectory(Directory parent, String name) throws IOException {
        return new FileBasedDirectory(parent, name, isImmutable);
    }

    @Override
    public Directory createDirectory(String name) throws IOException {
        return new FileBasedDirectory(root, name, isImmutable);
    }

    @Override
    public File createFile(Directory parent, String filename) throws IOException {
        return new FileBasedFile(parent, filename, isImmutable);
    }

    @Override
    public File createFile(Directory parent, String filename, Data data) throws IOException {
        return new FileBasedFile(parent, filename, data, isImmutable);
    }

    @Override
    public void destroy() throws DestroyException {
        try {
            FileUtils.deleteDirectory(root.toFile());
        } catch (IOException e) {
            throw new DestroyException("Unable to destroy root directory");
        }
    }

}
