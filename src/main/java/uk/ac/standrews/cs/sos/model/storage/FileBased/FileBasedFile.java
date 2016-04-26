package uk.ac.standrews.cs.sos.model.storage.FileBased;

import uk.ac.standrews.cs.sos.interfaces.storage.SOSDirectory;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedFile implements SOSFile {


    public FileBasedFile(SOSDirectory parent, String filename) {

    }

    @Override
    public SOSDirectory getParent() {
        return null;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public String getPathname() {
        return null;
    }
}
