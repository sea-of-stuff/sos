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

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.utils.LRU_GUID;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Maps the GUID for a manifest to a set of node refs that may have it.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsLocationsIndex implements Serializable {

    private transient HashMap<IGUID, Set<IGUID>> index;
    private transient LRU_GUID lru;

    private static final long serialVersionUID = 1L;
    public ManifestsLocationsIndex() {
        index = new HashMap<>();
        lru = new LRU_GUID();
    }

    public void addEntry(IGUID manifestGUID, IGUID node) {

        IGUID guidToRemove = lru.applyLRU(manifestGUID);
        if (!guidToRemove.isInvalid()) {
            index.remove(guidToRemove);
        }

        if (!index.containsKey(manifestGUID)) {
            index.put(manifestGUID, new HashSet<>());
        }

        index.get(manifestGUID).add(node);
    }

    public void evictEntry(IGUID manifestGUID, IGUID node) {

        if (index.containsKey(manifestGUID)) {
            Set<IGUID> nodes = index.get(manifestGUID);
            nodes.remove(node);

            if (nodes.isEmpty()) {
                index.remove(manifestGUID);
                lru.remove(manifestGUID);
            }
        }

    }

    public Set<IGUID> getNodeRefs(IGUID manifestGUID) {

        lru.applyReadLRU(manifestGUID);

        if (index.containsKey(manifestGUID)) {
            return index.get(manifestGUID);
        } else {
            return new LinkedHashSet<>();
        }
    }

    public void clear() {

        index.clear();
        lru.clear();
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(index.size());

        // Store entries as ordered in the LRU
        ConcurrentLinkedQueue<IGUID> lruQueue = new  ConcurrentLinkedQueue<>(lru.getQueue());
        IGUID guid;
        while ((guid = lruQueue.poll()) != null) {
            Set<IGUID> values = index.get(guid);

            out.writeUTF(guid.toMultiHash());
            out.writeInt(values.size());

            for(IGUID value:values) {
                out.writeUTF(value.toMultiHash());
            }
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        lru = new LRU_GUID();

        int indexSize = in.readInt();
        index = new HashMap<>();
        for(int i = 0; i < indexSize; i++) {
            try {
                IGUID key = GUIDFactory.recreateGUID(in.readUTF());

                int values = in.readInt();
                for(int j = 0; j < values; j++) {
                    IGUID value = GUIDFactory.recreateGUID(in.readUTF());
                    addEntry(key, value);
                }
            } catch (GUIDGenerationException e) {
                throw new IOException();
            }
        }
    }
}
