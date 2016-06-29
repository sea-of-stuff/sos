package uk.ac.standrews.cs.sos.storage.implementations.NetworkBased;

import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSFile;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NetworkBasedStorage implements Storage {

    private static final String VOLUMES_PATH = "/Volumes/";
    private String mountPoint;
    private String rootPath;

    private SOSDirectory root;

    /**
     * Create the Storage for a mounted network folder.
     * The storage should be mounted under /Volumes
     *
     * @param mountPoint
     * @param rootPath
     */
    public NetworkBasedStorage(String mountPoint, String rootPath) {
        if (mountPoint != null && !mountPoint.isEmpty() &&
                rootPath != null && !rootPath.isEmpty()) {
            this.mountPoint = mountPoint;
            this.rootPath = rootPath;

//            root = new NetworkBasedDirectory(VOLUMES_PATH +
//                    mountPoint + "/" + rootPath);
        }
    }

    @Override
    public SOSDirectory getRoot() {
        return root;
    }

    @Override
    public SOSDirectory getDataDirectory() {
        return null;
    }

    @Override
    public SOSDirectory getManifestDirectory() {
        return null;
    }

    @Override
    public SOSDirectory getTestDirectory() {
        return null;
    }

    @Override
    public SOSDirectory createDirectory(SOSDirectory parent, String name) {
        return null;
    }

    @Override
    public SOSDirectory createDirectory(String name) {
        return null;
    }

    @Override
    public SOSFile createFile(SOSDirectory parent, String filename) {
        return null;
    }
}
