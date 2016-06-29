package uk.ac.standrews.cs.sos.storage.implementations.filesystem;


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

    public FileBasedStorage(java.io.File rootDirectory) {
        root = new FileBasedDirectory(rootDirectory);
    }

    @Override
    public Directory getRoot() {
        return root;
    }

    @Override
    public Directory getDataDirectory() {
        return new FileBasedDirectory(root, DATA_DIRECTORY_NAME);
    }

    @Override
    public Directory getManifestDirectory() {
        return new FileBasedDirectory(root, MANIFESTS_DIRECTORY_NAME);
    }

    @Override
    public Directory getTestDirectory() {
        return new FileBasedDirectory(root, TEST_DATA_DIRECTORY_NAME);
    }

    @Override
    public Directory createDirectory(Directory parent, String name) {
        return new FileBasedDirectory(parent, name);
    }

    @Override
    public Directory createDirectory(String name) {
        return new FileBasedDirectory(root, name);
    }

    @Override
    public File createFile(Directory parent, String filename) {
        return new FileBasedFile(parent, filename);
    }
}
