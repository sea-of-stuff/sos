package uk.ac.standrews.cs.sos.filesystem.impl;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.fs.exceptions.*;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utils.UriUtil;

import java.net.URI;
import java.util.Iterator;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystem implements IFileSystem {

    private Agent sos;
    private IGUID invariant;

    public SOSFileSystem(Agent sos, Asset rootAsset) throws FileSystemCreationException {
        this.sos = sos;
        this.invariant = rootAsset.getInvariantGUID();

        try {
            SOSDirectory root = new SOSDirectory(sos, rootAsset);
            root.persist();
        } catch (PersistenceException e) {
            throw new FileSystemCreationException();
        }

        SOS_LOG.log(LEVEL.INFO, "WEBDAV - File System Created");
    }

    @Override
    public IFile createNewFile(IDirectory parent, String name, String content_type, IData data) throws BindingPresentException, PersistenceException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Create new file " + name);

        SOSFile file = new SOSFile(sos, (SOSDirectory) parent, data, null);
        file.persist();

        updateParent((SOSDirectory) parent, name, file);

        return file;
    }

    @Override
    public synchronized void updateFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, UpdateException, PersistenceException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Update file " + name);

        SOSFile previous = (SOSFile) parent.get(name);
        SOSFile file = new SOSFile(sos, (SOSDirectory) parent, data, previous);
        file.persist();

        updateParent((SOSDirectory) parent, name, file);
    }

    @Override
    public synchronized void appendToFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, AppendException, PersistenceException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Append to file " + name);

        // TODO - this is necessary for big files (> a few kb)
        throw new NotImplementedException();
    }

    @Override
    public IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Create new directory " + name);

        SOSDirectory directory = null;
        try {
            directory = new SOSDirectory(sos, (SOSDirectory) parent, name);
            directory.persist();
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }

        updateParent((SOSDirectory) parent, name, directory);

        return directory;
    }

    @Override
    public void deleteObject(IDirectory parent, String name) throws BindingAbsentException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Delete object " + name);

        try {
            // Create new parent object without child
            parent.remove(name);
            parent.persist();

            // Parent's parent
            SOSDirectory parentParent = (SOSDirectory) parent.getParent();
            String parentName = parent.getName();

            // Update tree updward
            updateParent(parentParent, parentName, (SOSDirectory) parent);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void moveObject(IDirectory source_parent, String source_name, IDirectory destination_parent, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Move object " + source_name + " TO " + destination_name);

        // TODO - delete and create
        throw new NotImplementedException();
    }

    @Override
    public void copyObject(IDirectory source_parent, String source_name, IDirectory destination_parent, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException, PersistenceException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Copy object " + source_name + " TO " + destination_name);

        IFileSystemObject object = source_parent.get(source_name);
        if (object instanceof IDirectory) {
            // TODO - Iterate over directory?
        } else {
            // TODO - get data from sourcename, create new resource at destination_name and put data there
        }

        throw new NotImplementedException();
    }

    @Override
    public IDirectory getRootDirectory() {
        Asset head = SOSFileSystemFactory.getRoot(sos, invariant);
        return new SOSDirectory(sos, head);
    }

    @Override
    public IGUID getRootId() {
        Asset asset = SOSFileSystemFactory.getRoot(sos, invariant);
        return asset.getVersionGUID();
    }

    @Override
    public IFileSystemObject resolveObject(URI uri) {

        Iterator iterator = UriUtil.pathElementIterator(uri);
        IDirectory parent = getRootDirectory();

        IFileSystemObject object = parent;

        while (iterator.hasNext()) {

            String name = (String) iterator.next();
            object = parent.get(name);
            if (object == null) {
                return null;  // No object with the current name.
            }

            object.setName(name);
            object.setParent(parent);

            try {
                if (iterator.hasNext()) {
                    parent = (IDirectory) object;
                }
            } catch (ClassCastException e) {
                // Current object isn't a directory, and we haven't reached the end of the path, so invalid path.
                return null;
            }
        }

        return object;
    }

    /**
     * Traverse the fs tree up recursively and update each directory that is found on the way
     * @param parent to be updated
     * @param name name of the object that changed
     * @param object object changed
     * @throws PersistenceException
     */
    private void updateParent(SOSDirectory parent, String name, SOSFileSystemObject object) throws PersistenceException {

        // Base case for recursion
        if (parent == null) {
            return;
        }

        // Update the parent directory
        SOSDirectory directory = new SOSDirectory(sos, parent, name, object);
        directory.persist();

        // Get the parent's parent directory
        SOSDirectory parentParent = (SOSDirectory) parent.getParent();
        String parentName = parent.getName();

        // Update the parent's parent directory
        updateParent(parentParent, parentName, directory);
    }
}
