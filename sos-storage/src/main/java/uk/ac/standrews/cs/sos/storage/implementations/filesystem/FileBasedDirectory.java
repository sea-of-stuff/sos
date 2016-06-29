package uk.ac.standrews.cs.sos.storage.implementations.filesystem;

import uk.ac.standrews.cs.sos.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.implementations.NameObjectBindingImpl;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.StatefulObject;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileBasedDirectory extends FileBasedStatefulObject implements Directory {

    private static final Logger log = Logger.getLogger(FileBasedDirectory.class.getName());



    public FileBasedDirectory(Directory parent, String name, boolean isImmutable) {
        super(parent, name, isImmutable);
        realFile = new java.io.File(parent.toFile(), name);
    }

    public FileBasedDirectory(java.io.File directory) {
        super();
        realFile = directory;
    }

    @Override
    public Directory getParent() {
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
    public void persist() throws PersistenceException {
        if (realFile.exists()) {
            if (!realFile.isDirectory()) {
                throw new PersistenceException(realFile.getAbsolutePath() + " is not a directory");
            }
        } else {
            createDirectory();
        }
    }

    private void createDirectory() throws PersistenceException {
        if (!realFile.mkdirs()) {
            throw new PersistenceException("Could not create directory " + realFile.getAbsolutePath());
        }
    }

    @Override
    public StatefulObject get(String name) throws BindingAbsentException {
        java.io.File candidate = new java.io.File(realFile, name);
        if (!candidate.exists()) {
            throw new BindingAbsentException("Object " + name + " is not present");
        }

        if (candidate.isFile()) {
            return new FileBasedFile(this, name, isImmutable);
        }

        if (candidate.isDirectory()) {
            return new FileBasedDirectory(this, name, isImmutable);
        }

        return null;
    }

    @Override
    public boolean contains(String name) {
        java.io.File candidate = new java.io.File(realFile, name);
        return candidate.exists();
    }

    @Override
    public void addSOSFile(File file, String name) {
        // Don't need to do anything since file can't be created in isolation from parent directory.
    }

    @Override
    public void addSOSDirectory(Directory directory, String name) {
        // Don't need to do anything since directory can't be created in isolation from parent directory.
    }

    @Override
    public void remove(String name) throws BindingAbsentException {
        java.io.File candidate = new java.io.File(realFile, name);
        if (!candidate.exists()) {
            throw new BindingAbsentException("file " + name + " not present");
        }

        candidate.delete();    // Ignore result - nothing to do with it.
    }

    @Override
    public Iterator<StatefulObject> getIterator() {
        return new DirectoryIterator(realFile);
    }

    private class DirectoryIterator<T> implements Iterator {

        String[] names;
        int index;

        public DirectoryIterator(java.io.File realFile) {

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
            StatefulObject obj = null;
            try {
                obj = get(name);
                index++;

                return new NameObjectBindingImpl(name, obj);
            } catch (BindingAbsentException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
