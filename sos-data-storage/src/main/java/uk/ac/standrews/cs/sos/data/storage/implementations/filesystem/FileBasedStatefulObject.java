package uk.ac.standrews.cs.sos.storage.implementations.filesystem;

import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.StatefulObject;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class FileBasedStatefulObject implements StatefulObject {

    protected Directory logicalParent;
    protected String name;
    protected File realFile;
    protected boolean isImmutable;

    public FileBasedStatefulObject(Directory parent, String name, boolean isImmutable) {
        this.logicalParent = parent;
        this.name = name;
        this.isImmutable = isImmutable;
    }

    public FileBasedStatefulObject() {
        this.name = "";
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
    public String getName() {
        return name;
    }

    @Override
    public abstract String getPathname();

    @Override
    public long lastModified() {
        return realFile.lastModified();
    }

    @Override
    public File toFile() {
        return realFile;
    }

    @Override
    public abstract void persist() throws PersistenceException;

}
