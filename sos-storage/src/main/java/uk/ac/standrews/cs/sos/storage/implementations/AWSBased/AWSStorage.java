package uk.ac.standrews.cs.sos.storage.implementations.AWSBased;

import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSFile;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSStorage implements Storage {

    public AWSStorage() {
        // TODO - connect to AWS
    }

    @Override
    public SOSDirectory getRoot() {
        return null;
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
