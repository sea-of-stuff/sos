package uk.ac.standrews.cs.sos.storage.implementations.FileBased;

import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSFile;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedFile extends FileBasedStatefulObject implements SOSFile {

    public FileBasedFile(SOSDirectory parent, String name) {
        super(parent, name);
        realFile = new File(parent.toFile(), name);
    }

    @Override
    public String getPathname() {
        return logicalParent.getPathname() + "/" + name;
    }

}
