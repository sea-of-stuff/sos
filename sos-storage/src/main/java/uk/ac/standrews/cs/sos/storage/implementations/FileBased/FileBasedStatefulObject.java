package uk.ac.standrews.cs.sos.storage.implementations.FileBased;

import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSStatefulObject;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedStatefulObject implements SOSStatefulObject{

    protected SOSDirectory logicalParent;
    protected String name;
    protected File realFile;

    public FileBasedStatefulObject(SOSDirectory parent, String name) {
        this.logicalParent = parent;
        this.name = name;
    }

    public FileBasedStatefulObject() {
        this.name = "";
    }

    @Override
    public SOSDirectory getParent() {
        return logicalParent;
    }

    @Override
    public boolean exists() {
        return realFile.exists();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPathname() {
        return null;
    }

    @Override
    public long lastModified() {
        return realFile.lastModified();
    }

    @Override
    public File toFile() {
        return realFile;
    }
}
