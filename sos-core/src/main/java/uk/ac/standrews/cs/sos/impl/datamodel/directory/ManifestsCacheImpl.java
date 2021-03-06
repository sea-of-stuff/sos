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
package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.IgnoreException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.utils.LRU_GUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.SecureAtom;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.Persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImpl extends AbstractManifestsDirectory implements ManifestsCache, Serializable {

    private transient HashMap<IGUID, Manifest> cache;
    private transient LRU_GUID lru;

    private static final long serialVersionUID = 1L;
    public ManifestsCacheImpl() {
        cache = new HashMap<>();
        lru = new LRU_GUID();
    }

    public ManifestsCacheImpl(int size) {
        cache = new HashMap<>();
        lru = new LRU_GUID(size);
    }

    @Override
    public synchronized void addManifest(Manifest manifest) {

        IGUID guid = manifest.guid();

        IGUID guidToRemove = lru.applyLRU(guid);
        if (!guidToRemove.isInvalid()) {
            cache.remove(guidToRemove);
        }

        if (manifest.getType().equals(ManifestType.ATOM)) {

            // Check if there is already an atom in the cache.
            try {
                Atom retrievedManifest = (Atom) findManifest(guid);
                manifest = mergeManifests(guid, (Atom) manifest, retrievedManifest);

            } catch (ManifestNotFoundException e) {
                // DO NOTHING
            }
        } else if (manifest.getType().equals(ManifestType.ATOM_PROTECTED)) {

            // Check if there is already an atom in the cache.
            try {
                SecureAtom retrievedManifest = (SecureAtom) findManifest(guid);
                manifest = mergeManifests(guid, (SecureAtom) manifest, retrievedManifest);

            } catch (ManifestNotFoundException e) {
                // DO NOTHING
            }
        }

        cache.put(guid, manifest);
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

        if (cache == null) {
            throw new ManifestNotFoundException("Cache has not been initialised");
        }

        if (!cache.containsKey(guid)) {
            throw new ManifestNotFoundException("Unable to find manifest for GUID: " + guid.toShortString() + " in the cache");
        }

        lru.applyReadLRU(guid);

        return cache.get(guid);
    }

    @Override
    public void delete(IGUID guid) throws ManifestNotFoundException {

        if (cache.containsKey(guid)) {
            cache.remove(guid);
            lru.remove(guid);
        } else {
            throw new ManifestNotFoundException("Manifest with GUID "  + guid.toMultiHash() + " was not found and could not be deleted.");
        }
    }

    @Override
    public void flush() {
        // NOTE: This method is not implemented, as we use the persist method to actually flush the cache
    }

    @Override
    public LRU_GUID getLRU() {
        return lru;
    }

    @Override
    public void clear() {

        cache.clear();
        lru.clear();
    }

    public static ManifestsCache load(LocalStorage storage, IFile file, IDirectory manifestsDir) throws IOException, ClassNotFoundException, IgnoreException {

        ManifestsCache persistedCache = (ManifestsCache) Persistence.load(file);

        if (persistedCache == null) throw new ClassNotFoundException();
        if (persistedCache.getLRU() == null) return persistedCache;

        // Reload manifests this way rather than through serialization.
        // Make a copy of the queue, so that the LRU queue is not changed by the poll() method
        ConcurrentLinkedQueue<IGUID> lruQueue = new  ConcurrentLinkedQueue<>(persistedCache.getLRU().getQueue());
        IGUID guid;
        while ((guid = lruQueue.poll()) != null) {
            Manifest manifest = loadManifest(storage, manifestsDir, guid);
            if (manifest != null) {
                try {
                    persistedCache.addManifest(manifest);
                } catch (ManifestPersistException e) {
                    throw new IOException("Unable to load manifest correctly " + manifest);
                }
            }
        }

        return persistedCache;
    }

    private static Manifest loadManifest(LocalStorage storage, IDirectory manifestsDir, IGUID guid) {
        try {
            IFile fileRef = FileUtils.CreateFile(storage, manifestsDir, guid.toMultiHash());
            return FileUtils.ManifestFromFile(fileRef);
        } catch (DataStorageException | ManifestNotFoundException e) {
            return null;
        }

    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeObject(lru);
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        lru = (LRU_GUID) in.readObject();
        cache = new HashMap<>();
    }
}
