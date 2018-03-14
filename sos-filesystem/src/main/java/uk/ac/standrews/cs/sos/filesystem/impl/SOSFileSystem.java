/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module filesystem.
 *
 * filesystem is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * filesystem is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with filesystem. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.fs.exceptions.*;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemException;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.services.Agent;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.network.UriUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The SOSFileSystem class maps the interface for the real FS operations to the SOS FS.
 * The SOS FS is made of SOSFiles and SOSDirectories.
 * SOSFiles are asset of atoms, while SOSDirectories are asset of compounds.
 *
 * The SOS FS is a FS of immutable entities, therefore all update operations (update file, remove object, etc)
 * are actually creating new state of the SOS and linking it to the previous entities of the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystem implements IFileSystem {

    private Agent sos;
    private IGUID invariant;

    private HashMap<String, SOSFile> pendingFiles;

    public SOSFileSystem(Agent sos, Version rootVersion) throws FileSystemCreationException {
        this.sos = sos;
        this.invariant = rootVersion.invariant();

        try {
            SOSDirectory root = new SOSDirectory(sos, rootVersion);
            root.persist();
        } catch (PersistenceException e) {
            throw new FileSystemCreationException();
        }


        pendingFiles = new HashMap<>();
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - CreateFile System Created");
    }

    @Override
    public IFile createNewFile(IDirectory parent, String name, String content_type, IData data) throws BindingPresentException, PersistenceException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Create new file " + name);

        // TODO - satisfied if file exists already. it is often the case with the DS store files at start?
        try {
            long size = data.getSize();

//        if (data.getSize() != 0) {
            SOSFile file = new SOSFile(sos, (SOSDirectory) parent, data, null);
            file.persist();

            updateParent((SOSDirectory) parent, name, file);

            return file;
//        } else {
//            SOSFile file = new SOSFile(sos);
//            pendingFiles.put(parent.getGUID() + "/" + name, file);
//            return file;
//        }

        } catch (SOSFileSystemException e) {
            throw new PersistenceException("SOSFS Exception");
        }
    }

    @Override
    public synchronized void updateFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, UpdateException, PersistenceException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Update file " + name);

        try {

            boolean compoundFile = pendingFiles.containsKey(parent.getGUID() + "/" + name);
            if (compoundFile) {
                SOSFile file = pendingFiles.get(parent.getGUID() + "/" + name);
                file.append(data);
            } else {
                SOSFile previous = (SOSFile) parent.get(name);
                SOSFile file = new SOSFile(sos, (SOSDirectory) parent, data, previous);
                file.persist();

                updateParent((SOSDirectory) parent, name, file);
            }

        } catch (SOSFileSystemException e) {
            throw new PersistenceException("SOSFS Exception");
        }
    }

    @Override
    public synchronized void appendToFile(IDirectory parent, String name, String content_type, IData data) throws BindingAbsentException, AppendException, PersistenceException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Append to file " + name);

        long size = data.getSize();

        SOSFile file = pendingFiles.get(parent.getGUID() + "/" + name);
        file.append(data);
    }

    /**
     * Creates a directory with a given name in path: parent/name
     * @param parent
     * @param name
     * @return
     * @throws BindingPresentException
     * @throws PersistenceException
     */
    @Override
    public IDirectory createNewDirectory(IDirectory parent, String name) throws BindingPresentException, PersistenceException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Create new directory " + name);

        try {
            SOSDirectory directory = new SOSDirectory(sos, name, (SOSDirectory) parent);
            directory.persist();

            updateParent((SOSDirectory) parent, name, directory);

            return directory;
        } catch (SOSFileSystemException e) {
            throw new PersistenceException("Unable to create SOS Directory correctly");
        }
    }

    @Override
    public void deleteObject(IDirectory parent, String name) throws BindingAbsentException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Delete object " + name + " from parent " + parent.getName());

        try {
            // Create a new directory using the PARENT,
            // where the named object is removed and the PARENT becomes the previous of this new object
            SOSDirectory directory = new SOSDirectory(sos, (SOSDirectory) parent, name);
            directory.persist();

            // Get Parent's parent and update tree upward
            SOSDirectory parentParent = (SOSDirectory) parent.getParent();
            String parentName = parent.getName();
            updateParent(parentParent, parentName, directory);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void moveObject(IDirectory source_parent, String source_name, IDirectory destination_parent, String destination_name, boolean overwrite) throws BindingAbsentException, BindingPresentException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Move object " + source_name + " TO " + destination_name);

        // TODO - delete and create
        throw new UnsupportedOperationException();
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

        throw new UnsupportedOperationException();
    }

    @Override
    public IDirectory getRootDirectory() {
        Version tip = SOSFileSystemFactory.getRoot(sos, invariant);
        return new SOSDirectory(sos, tip);
    }

    @Override
    public IGUID getRootId() {
        Version version = SOSFileSystemFactory.getRoot(sos, invariant);
        return version.version();
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
     * @param name name of the newObject
     * @param newObject
     * @throws PersistenceException
     */
    private void updateParent(SOSDirectory parent, String name, SOSFileSystemObject newObject) throws PersistenceException {

        // Base case for recursion
        if (parent == null) {
            return;
        }

        // Create an updated parent directory with the new newObject
        SOSDirectory directory = new SOSDirectory(sos, parent, name, newObject);
        directory.persist();

        // Get the parent's parent directory
        SOSDirectory parentParent = (SOSDirectory) parent.getParent();
        String parentName = parent.getName();

        // Update the parent's parent directory
        updateParent(parentParent, parentName, directory);
    }
}
