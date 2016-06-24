package uk.ac.standrews.cs.sos.storage.FileBased;

import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSFile;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSStatefulObject;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedDirectory implements SOSDirectory {

    private SOSDirectory parent;
    private String name;

    private HashSet<SOSStatefulObject> children;

    public FileBasedDirectory(SOSDirectory parent, String name) {
        this.parent = parent;
        this.name = name;

        children = new LinkedHashSet<>();
    }

    public FileBasedDirectory(String name) {
        this.parent = new FileBasedDirectory();
        this.name = name;

        children = new LinkedHashSet<>();
    }

    private FileBasedDirectory() {}

    @Override
    public SOSDirectory getParent() {
        return parent;
    }

    @Override
    public boolean exists() {
        return new File(getPathname()).exists();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPathname() {
        if (parent == null) {
            return "";
        } else if (name == null || name.isEmpty()) {
            return parent.getPathname() + "/";
        } else {
            return parent.getPathname() + name + "/";
        }
    }

    @Override
    public long lastModified() {
        return new File(getPathname()).lastModified();
    }

    @Override
    public SOSFile addSOSFile(String fileName) {
        SOSFile file = new FileBasedFile(this, fileName);
        if (file != null) {
            boolean added = children.add(file);
            if (added) {
                return file;
            }
        }

        return null;
    }

    @Override
    public SOSDirectory addSOSDirectory(String directoryName) {
        SOSDirectory directory = new FileBasedDirectory(this, directoryName);
        boolean added = children.add(directory);
        if (added) {
            return directory;
        } else {
            return null;
        }
    }

    @Override
    public Iterator<SOSStatefulObject> getIterator() {
        return children.iterator();
    }

    @Override
    public boolean mkdirs() {
        return new File(getPathname()).mkdirs();
    }

}
