package uk.ac.standrews.cs.sos.storage.implementations.aws;

import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSStorage implements Storage {

    public AWSStorage() {
        // TODO - connect to AWS
    }

    @Override
    public Directory getRoot() {
        return null;
    }

    @Override
    public Directory getDataDirectory() {
        return null;
    }

    @Override
    public Directory getManifestDirectory() {
        return null;
    }

    @Override
    public Directory getTestDirectory() {
        return null;
    }

    @Override
    public Directory createDirectory(Directory parent, String name) {
        return null;
    }

    @Override
    public Directory createDirectory(String name) {
        return null;
    }

    @Override
    public File createFile(Directory parent, String filename) {
        return null;
    }
}
