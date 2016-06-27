package uk.ac.standrews.cs.sos.storage.implementations.FileBased;


import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSFile;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedStorage implements Storage {

    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    private static final String TEST_DATA_DIRECTORY_NAME = "test_data";

    private SOSDirectory root;

    public FileBasedStorage(String location) {
        if (location != null && !location.isEmpty()) {
            root = new FileBasedDirectory(location);
        }
    }

    @Override
    public SOSDirectory getRoot() {
        return root;
    }

    @Override
    public SOSDirectory getDataDirectory() {
        return new FileBasedDirectory(root, DATA_DIRECTORY_NAME);
    }

    @Override
    public SOSDirectory getManifestDirectory() {
        return new FileBasedDirectory(root, MANIFESTS_DIRECTORY_NAME);
    }

    @Override
    public SOSDirectory getTestDirectory() {
        return new FileBasedDirectory(root, TEST_DATA_DIRECTORY_NAME);
    }

    @Override
    public SOSDirectory createDirectory(SOSDirectory parent, String name) {
        return new FileBasedDirectory(parent, name);
    }

    @Override
    public SOSDirectory createDirectory(String name) {
        return new FileBasedDirectory(name);
    }

    @Override
    public SOSFile createFile(SOSDirectory parent, String filename) {
        return new FileBasedFile(parent, filename);
    }
}
