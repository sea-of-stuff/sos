package uk.ac.standrews.cs.sos.filesystem.impl;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.fs.exceptions.*;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.filesystem.util.UriUtil;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.utils.LOG;

import java.net.URI;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystem implements IFileSystem {

    private Client sos;
    private IGUID invariant;

    public SOSFileSystem(Client sos, Version root) {
        this.sos = sos;
        this.invariant = root.getInvariantGUID();

        try {
            SOSDirectory root_collection = new SOSDirectory(sos, root);
            root_collection.persist();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }

        LOG.log(LEVEL.INFO, "WEBDAV - File System Created");
    }

    // TODO - create compound if large file
    // maybe have a different call for large files
    // via appendToFile
    @Override
    public IFile createNewFile(IDirectory parent, String name, String content_type, IData data) throws BindingPresentException, PersistenceException {
        LOG.log(LEVEL.INFO, "WEBDAV - Create new file " + name);

        // TODO - check if file already exists.
        // if it does, then throw exception BindingPresentException
        // should check against sos. So it will be a check by content, not by name
        // not sure how this will work because the stream will have to be consumed
        // check(parent, name, ...) // look at StoreBasedFileSystem

        SOSFile file = new SOSFile(sos, (SOSDirectory) parent, data);
        file.persist();

        updateParent((SOSDirectory) parent, name, file);

        return file;
    }

    // TODO - should override
    //  this should be in IFile system
    // This way we could have a uniform way of dealing with large data (chunked)
    public IFile createNewFile(IDirectory parent, String name, String content_type) throws BindingPresentException, PersistenceException {
        // TODO - check if file already exists.
        // NOTE - add file to parent only when the file is persisted.
        IFile file = new SOSFile(sos);
        return file;
    }

    @Override
    public synchronized void updateFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, UpdateException, PersistenceException {
        LOG.log(LEVEL.INFO, "WEBDAV - Update file " + name);

        SOSFile previous = (SOSFile) parent.get(name);
        SOSFile file = new SOSFile(sos, (SOSDirectory) parent, data, previous);
        file.persist();

        updateParent((SOSDirectory) parent, name, file);
    }

    @Override
    public synchronized void appendToFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, AppendException, PersistenceException {
        LOG.log(LEVEL.INFO, "WEBDAV - Append to file " + name);
        // NOTE call a series of append calls on SOSFile and end it with a persist - behaviour is different from the one in abstract file system
    }

    @Override
    public IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException {
        LOG.log(LEVEL.INFO, "WEBDAV - Create new directory " + name);
        // TODO - should check if directory already exists

        SOSDirectory directory = null;
        try {
            directory = new SOSDirectory(sos, (SOSDirectory) parent, name);
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }
        directory.persist();

        updateParent((SOSDirectory) parent, name, directory);

        return directory;
    }

    private void updateParent(SOSDirectory parent, String name, SOSFileSystemObject object) throws PersistenceException {
        if (parent == null) {
            return;
        }

        LOG.log(LEVEL.INFO, "WEBDAV - update directory (invariant) " + parent.getInvariant());
        SOSDirectory directory = new SOSDirectory(sos, parent, name, object);
        directory.persist();

        updateParent((SOSDirectory) parent.getParent(), parent.name, directory);
    }

    @Override
    public void deleteObject(IDirectory parent, String name) throws BindingAbsentException {
        LOG.log(LEVEL.INFO, "WEBDAV - Delete object " + name);
        // TODO - This will add a version to the asset with no content
        throw new NotImplementedException();
    }

    @Override
    public void moveObject(IDirectory source_parent, String source_name, IDirectory destination_parent, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException {
        LOG.log(LEVEL.INFO, "WEBDAV - Move object " + source_name + " TO " + destination_name);

        throw new NotImplementedException();
    }

    @Override
    public void copyObject(IDirectory source_parent, String source_name, IDirectory destination_parent, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException, PersistenceException {
        LOG.log(LEVEL.INFO, "WEBDAV - Copy object " + source_name + " TO " + destination_name);

        throw new NotImplementedException();
    }

    @Override
    public IDirectory getRootDirectory() {
        // LOG.log(LEVEL.INFO, "WEBDAV - Get root");

        try {
            Version head = sos.getHEAD(invariant);
            return new SOSDirectory(sos, head);
        } catch (HEADNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public IGUID getRootId() {
        return invariant;
    }

    @Override
    public IAttributedStatefulObject resolveObject(URI uri) {
        // LOG.log(LEVEL.INFO, "WEBDAV - Resolving object with URI " + uri.toString());

        Iterator iterator = UriUtil.pathElementIterator(uri);
        IDirectory parent = getRootDirectory();

        IAttributedStatefulObject object = parent;

        while (iterator.hasNext()) {

            String name = (String) iterator.next();
            object = parent.get(name);

            if (object == null)
                return null;  // No object with the current name.

            try {
                if (iterator.hasNext())
                    parent = (IDirectory) object;
            } catch (ClassCastException e) {
                return null;  // Current object isn't a directory, and we haven't reached the end of the path, so invalid path.
            }
        }

        return object;
    }
}
