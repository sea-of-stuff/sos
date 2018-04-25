/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;

/**
 * This is a SOS specific wrapper on the IStorage module.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalStorage {

    private static final String ATOM_DIRECTORY_NAME = "atom";
    private static final String MANIFESTS_DIRECTORY_NAME = "manifests";
    private static final String NODE_DIRECTORY_NAME = "node"; // where all internal data structures and setting files are stored
    private static final String JAVA_DIRECTORY_NAME = "java";
    private static final String KEYS_DIRECTORY_NAME = "keys";

    // The actual storage used by this node
    private IStorage storage;

    /**
     * Construct the storage space used by this node.
     *
     * @param storage
     * @throws DataStorageException
     */
    public LocalStorage(IStorage storage) throws DataStorageException {
        this.storage = storage;

        createSOSDirectories();
    }

    /**
     * Return the directory used to store atoms
     * @return data directory
     * @throws DataStorageException
     */
    public IDirectory getAtomsDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(ATOM_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Return the directory used to store the manifests
     * @return manifest directory
     * @throws DataStorageException
     */
    public IDirectory getManifestsDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(MANIFESTS_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Return the directory used to store the caches
     * @return caches directory
     * @throws DataStorageException
     */
    public IDirectory getNodeDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(NODE_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    public IDirectory getKeysDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(KEYS_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    public IDirectory getJavaDirectory() throws DataStorageException {
        try {
            return storage.createDirectory(JAVA_DIRECTORY_NAME);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Create an arbitrary file in a given directory
     * @param parent
     * @param filename
     * @return file being created
     * @throws DataStorageException
     */
    public IFile createFile(IDirectory parent, String filename) throws DataStorageException {
        try {
            return storage.createFile(parent, filename);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Create an arbitrary file in a given directory and some data
     * @param parent
     * @param filename
     * @param data
     * @return file being created
     * @throws DataStorageException
     */
    public IFile createFile(IDirectory parent, String filename, Data data) throws DataStorageException {
        try {
            return storage.createFile(parent, filename, data);
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Destroy the internal storage and its content.
     * This method should be used for test purposes only.
     *
     * @throws DataStorageException
     */
    public void destroy() throws DataStorageException {

        try {
            remove(NODE_DIRECTORY_NAME);
            remove(ATOM_DIRECTORY_NAME);
            remove(MANIFESTS_DIRECTORY_NAME);
            remove(JAVA_DIRECTORY_NAME);
            remove(KEYS_DIRECTORY_NAME);
        } catch (BindingAbsentException e) {
            throw new DataStorageException(e);
        }
    }

    private void remove(String directoryName) throws BindingAbsentException {
        if (storage.getRoot().contains(directoryName)) {
            storage.getRoot().remove(directoryName);
        }
    }

    private void createSOSDirectories() throws DataStorageException {
        try {
            storage.createDirectory(ATOM_DIRECTORY_NAME).persist();
            storage.createDirectory(MANIFESTS_DIRECTORY_NAME).persist();
            storage.createDirectory(NODE_DIRECTORY_NAME).persist();
            storage.createDirectory(JAVA_DIRECTORY_NAME).persist();
            storage.createDirectory(KEYS_DIRECTORY_NAME).persist();
        } catch (StorageException e) {
            throw new DataStorageException(e);
        }
    }
}
