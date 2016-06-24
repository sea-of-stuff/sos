package uk.ac.standrews.cs.sos.storage;

import uk.ac.standrews.cs.sos.storage.implementations.FileBased.FileBasedStorage;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageFactory {

    public static Storage createStorage(StorageType type, String location) {
        Storage storage = null;

        switch(type) {
            case LOCAL:
                storage = new FileBasedStorage(location);
                break;
            case NETWORK:
                // TODO
                break;
            case AWS_S3:
                // TODO
                break;
            default:
                System.out.println("I should throw an error, but instead I will just tell you I do not know this type of storage");
                break;
        }

        return storage;
    }

}
