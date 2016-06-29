package uk.ac.standrews.cs.sos.storage.implementations.FileBased;

import uk.ac.standrews.cs.sos.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.sos.storage.implementations.NameObjectBinding;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSFile;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSStatefulObject;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedDirectory extends FileBasedStatefulObject implements SOSDirectory {

    private static final Logger log= Logger.getLogger( FileBasedDirectory.class.getName() );

    public FileBasedDirectory(SOSDirectory parent, String name) {
        super(parent, name);
        realFile = new File(parent.toFile(), name);
    }

    public FileBasedDirectory(File directory) {
        super();
        realFile = directory;
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
    public String getPathname() {
        if (logicalParent == null) {
            return realFile.getAbsolutePath() + "/";
        } else if (name == null || name.isEmpty()) {
            return logicalParent.getPathname() + "/";
        } else {
            return logicalParent.getPathname() + name + "/";
        }
    }

    @Override
    public SOSStatefulObject get(String name) throws BindingAbsentException {
        File candidate = new File(realFile, name);
        if (!candidate.exists()) {
            throw new BindingAbsentException("Object " + name + " is not present");
        }

        if (candidate.isFile()) {
            return new FileBasedFile(this, name);
        }

        if (candidate.isDirectory()) {
            return new FileBasedDirectory(this, name);
        }

        return null;
    }

    @Override
    public boolean contains(String name) {
        File candidate = new File(realFile, name);
        return candidate.exists();
    }

    @Override
    public void addSOSFile(SOSFile file, String name) {
        // Don't need to do anything since file can't be created in isolation from parent directory.
    }

    @Override
    public void addSOSDirectory(SOSDirectory directory, String name) {
        // Don't need to do anything since directory can't be created in isolation from parent directory.
    }

    @Override
    public void remove(String name) throws BindingAbsentException {
        File candidate = new File(realFile, name);
        if (!candidate.exists())
            throw new BindingAbsentException("file " + name + " not present");

        candidate.delete();    // Ignore result - nothing to do with it.
    }

    @Override
    public Iterator<SOSStatefulObject> getIterator() {
        return new DirectoryIterator(realFile);
    }

    @Override
    public boolean mkdir() {
        return realFile.mkdirs();
    }

    private class DirectoryIterator implements Iterator {

        String[] names;
        int index;

        public DirectoryIterator(File realFile) {

            names = realFile.list();
            if (names == null) {
                log.log(Level.FINE, "File " + realFile.getPath() + " is not a directory");
            }
            index = 0;
        }

        public void remove() {
            log.log(Level.FINE, "Unimplemented method");
        }

        public boolean hasNext() {
            return index < names.length;
        }

        public Object next() {

            String name = names[index];
            SOSStatefulObject obj = null;
            try {
                obj = get(name);
                index++;

                return new NameObjectBinding(name, obj);
            } catch (BindingAbsentException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
