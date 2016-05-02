package uk.ac.standrews.cs.sos.model.storage.FileBased;

import uk.ac.standrews.cs.sos.interfaces.storage.SOSDirectory;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedFile implements SOSFile {


    private SOSDirectory parent;
    private String filename;

    private File file;

    public FileBasedFile(SOSDirectory parent, String filename) {
        this.parent = parent;
        this.filename = filename;

        file = new File(getPathname());
    }

    @Override
    public SOSDirectory getParent() {
        return parent;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public String getPathname() {
        return parent.getPathname() + filename;
    }

    @Override
    public File toFile() {
        return file;
    }

}
