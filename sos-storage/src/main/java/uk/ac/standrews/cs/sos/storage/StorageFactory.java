package uk.ac.standrews.cs.sos.storage;

import uk.ac.standrews.cs.sos.storage.FileBased.FileBasedStorage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageFactory {

    // TODO - create its own class
    public final static String S_TYPE_LOCAL = "local";
    public final static String S_TYPE_NETWORK = "network";
    public final static String S_TYPE_AWS_S3 = "aws_s3";

    public static Storage createStorage(String type, String location) {
        Storage storage = null;

        switch(type) {
            case S_TYPE_LOCAL:
                storage = new FileBasedStorage(location);
                break;
            case S_TYPE_NETWORK:
                // TODO
                break;
            case S_TYPE_AWS_S3:
                // TODO
                break;
            default:
                System.out.println("I should throw an error, but instead I will just tell you I do not know this type of storage");
                break;
        }

        return storage;
    }

}
