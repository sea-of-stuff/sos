package uk.ac.standrews.cs.sos.storage;

import uk.ac.standrews.cs.sos.storage.exceptions.StorageException;
import uk.ac.standrews.cs.sos.storage.implementations.filesystem.FileBasedStorage;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageFactory {

    private static final Logger log = Logger.getLogger( StorageFactory.class.getName() );

    public static Storage createStorage(StorageType type, String location) throws StorageException {
        Storage storage = null;

        switch(type) {
            case LOCAL:
                storage = new FileBasedStorage(new File(location));
                break;
            case NETWORK:
                // TODO
                break;
            case AWS_S3:
                // TODO
                break;
            default:
                log.log(Level.SEVERE, "Storage type: " + type + " is unknown. Impossible to create a storage.");
                throw new StorageException();
        }

        return storage;
    }

}
