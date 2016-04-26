package uk.ac.standrews.cs.sos.model.storage.FileBased;

import uk.ac.standrews.cs.sos.interfaces.storage.SOSDirectory;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSStatefulObject;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedDirectory implements SOSDirectory {

    public FileBasedDirectory(SOSDirectory parent, String name) {

    }

    public FileBasedDirectory(String name) {

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
    public SOSFile addSOSFile(String fileName) {
        return null;
    }

    @Override
    public SOSDirectory addSOSDirectory(String directoryName) {
        return null;
    }

    @Override
    public Iterator<SOSStatefulObject> getIterator() {
        return null;
    }

}
