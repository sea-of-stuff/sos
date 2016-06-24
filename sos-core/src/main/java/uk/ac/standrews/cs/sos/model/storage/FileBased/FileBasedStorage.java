package uk.ac.standrews.cs.sos.model.storage.FileBased;

import uk.ac.standrews.cs.sos.interfaces.storage.SOSDirectory;
import uk.ac.standrews.cs.sos.model.storage.Storage;
import uk.ac.standrews.cs.sos.node.Config;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedStorage implements Storage {

    private static final String DATA_DIRECTORY_NAME = "data";
    private static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    private static final String TEST_DATA_DIRECTORY_NAME = "test_data";

    private SOSDirectory root;

    public FileBasedStorage(Config config) {
        if (config.s_location != null && !config.s_location.isEmpty()) {
            root = new FileBasedDirectory(config.s_location);
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
}
