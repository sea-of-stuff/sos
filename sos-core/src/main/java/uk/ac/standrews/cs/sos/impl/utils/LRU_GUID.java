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
package uk.ac.standrews.cs.sos.impl.utils;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LRU_GUID implements Serializable {

    // Maximum number of manifests kept in the cache at one time
    private transient static final int MAX_DEFAULT_SIZE = 8192;

    private transient int size;
    private transient ConcurrentLinkedQueue<IGUID> lru;

    private static final long serialVersionUID = 1L;
    public LRU_GUID() {
        this(MAX_DEFAULT_SIZE);
    }

    public LRU_GUID(int size) {
        this.size = size;
        lru = new ConcurrentLinkedQueue<>();
    }

    public IGUID applyLRU(IGUID guid) {
        IGUID guidToRemove = new InvalidID();

        int currentCacheSize = lru.size();
        if (currentCacheSize >= size) {
            guidToRemove = lru.poll();
        }

        applyReadLRU(guid);

        return guidToRemove;
    }

    // Move guid to the top
    public void applyReadLRU(IGUID guid) {
        lru.remove(guid);
        lru.add(guid);
    }

    public void remove(IGUID guid) {
        lru.remove(guid);
    }

    public ConcurrentLinkedQueue<IGUID> getQueue() {
        return lru;
    }

    public void clear() {

        lru.clear();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(size);
        out.writeInt(lru.size());
        for (IGUID guid : lru) {
            out.writeUTF(guid.toMultiHash());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        try {
            size = in.readInt();
            lru = new ConcurrentLinkedQueue<>();
            int lruSize = in.readInt();
            for (int i = 0; i < lruSize; i++) {
                String guid = in.readUTF();
                lru.add(GUIDFactory.recreateGUID(guid));
            }

        } catch (GUIDGenerationException e) {
            SOS_LOG.log(LEVEL.WARN, "LRU loading issue with recreating GUIDs");
        }
    }
}
