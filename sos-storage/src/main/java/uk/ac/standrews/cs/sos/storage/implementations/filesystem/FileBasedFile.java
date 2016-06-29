package uk.ac.standrews.cs.sos.storage.implementations.filesystem;

import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedFile extends FileBasedStatefulObject implements File {

    public FileBasedFile(Directory parent, String name) {
        super(parent, name);
        realFile = new java.io.File(parent.toFile(), name);
    }

    @Override
    public String getPathname() {
        return logicalParent.getPathname() + "/" + name;
    }

}
