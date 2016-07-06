package uk.ac.standrews.cs.sos.storage;

import uk.ac.standrews.cs.sos.storage.exceptions.StorageException;
import uk.ac.standrews.cs.sos.storage.implementations.aws.AWSStorage;
import uk.ac.standrews.cs.sos.storage.implementations.filesystem.FileBasedStorage;
import uk.ac.standrews.cs.sos.storage.implementations.network.NetworkBasedStorage;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageFactory {

    private static final Logger log = Logger.getLogger( StorageFactory.class.getName() );

    /**
     * @param type
     * @param root (location for LOCAL, NETWORK; bucketname for AWS)
     * @param immutable
     * @return
     * @throws StorageException
     */
    public static Storage createStorage(StorageType type, String root, boolean immutable) throws StorageException {
        Storage storage = null;

        switch(type) {
            case LOCAL:
                storage = new FileBasedStorage(new File(root), immutable);
                break;
            case NETWORK:
                storage = new NetworkBasedStorage(root, root, immutable); // FIXME - parameters
                break;
            case AWS_S3:
                storage = new AWSStorage(root, immutable);
                break;
            default:
                log.log(Level.SEVERE, "Storage type: " + type + " is unknown. Impossible to create a storage.");
                throw new StorageException();
        }

        return storage;
    }

}
