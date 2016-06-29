package uk.ac.standrews.cs.sos.storage.implementations.filesystem;


import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedStorage implements Storage {

    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    private static final String TEST_DATA_DIRECTORY_NAME = "test_data";

    private Directory root;
    private boolean isImmutable;

    public FileBasedStorage(java.io.File rootDirectory, boolean isImmutable) {
        root = new FileBasedDirectory(rootDirectory);
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
    public Directory getDataDirectory() {
        return new FileBasedDirectory(root, DATA_DIRECTORY_NAME, isImmutable);
    }

    @Override
    public Directory getManifestDirectory() {
        return new FileBasedDirectory(root, MANIFESTS_DIRECTORY_NAME, isImmutable);
    }

    @Override
    public Directory getTestDirectory() {
        return new FileBasedDirectory(root, TEST_DATA_DIRECTORY_NAME, isImmutable);
    }

    @Override
    public Directory createDirectory(Directory parent, String name) {
        return new FileBasedDirectory(parent, name, isImmutable);
    }

    @Override
    public Directory createDirectory(String name) {
        return new FileBasedDirectory(root, name, isImmutable);
    }

    @Override
    public File createFile(Directory parent, String filename) {
        return new FileBasedFile(parent, filename, isImmutable);
    }

    @Override
    public File createFile(Directory parent, String filename, Data data) {
        return new FileBasedFile(parent, filename, data, isImmutable);
    }
}
