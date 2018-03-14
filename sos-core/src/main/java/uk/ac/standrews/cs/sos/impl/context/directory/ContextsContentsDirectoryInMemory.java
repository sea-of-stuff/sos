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
package uk.ac.standrews.cs.sos.impl.context.directory;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.utils.LRU_GUID;
import uk.ac.standrews.cs.sos.interfaces.context.ContextsContentsDirectory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * The ContextsDirectory caches information regarding contexts and their contents.
 *
 * Evicted entries allow us to re-use old results if the HEAD of an asset is changed.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextsContentsDirectoryInMemory implements Serializable, ContextsContentsDirectory {

    // Maps the context to the versions belonging to it
    // [ context -> [version -> ContextVersionInfo] ]
    private transient HashMap<IGUID, HashMap<IGUID, ContextVersionInfo>> mappings;
    private transient LRU_GUID lru;

    private static final long serialVersionUID = 1L;
    ContextsContentsDirectoryInMemory() {
        mappings = new HashMap<>();
        lru = new LRU_GUID();
    }

    @Override
    public void addOrUpdateEntry(IGUID contextInvariant, IGUID version, ContextVersionInfo content) {

        IGUID guidToRemove = lru.applyLRU(contextInvariant);
        if (!guidToRemove.isInvalid()) {
            mappings.remove(guidToRemove);
        }

        if (!mappings.containsKey(contextInvariant)) {
            mappings.put(contextInvariant, new HashMap<>());
        }

        mappings.get(contextInvariant).put(version, content);
    }

    /**
     * Get the known values for the version at the given context
     *
     * @param context
     * @param version
     * @return
     */
    @Override
    public ContextVersionInfo getEntry(IGUID context, IGUID version) {

        lru.applyReadLRU(context);

        if (entryExists(context, version)) {
            return mappings.get(context).get(version);
        } else {
            return new ContextVersionInfo();
        }
    }

    public void remove(IGUID context, IGUID version) {

        if (entryExists(context, version)) {
            HashMap<IGUID, ContextVersionInfo> mappedVersions = mappings.get(context);
            mappedVersions.remove(version);

            if (mappedVersions.isEmpty()) {
                mappings.remove(context);
                lru.remove(context);
            }
        }
    }

    /**
     * Checks if the version has already been applied for the given context
     *
     * This method ignores the eviction model. This way, we won't have to re-process the context for old entries as long as these are kept in this directory.
     *
     * @param context to check
     * @param version to check
     * @return true if the pair context-version has been processed already
     */
    @Override
    public boolean entryExists(IGUID context, IGUID version) {

        return mappings.containsKey(context) && mappings.get(context).containsKey(version);
    }

    @Override
    public Map<IGUID, ContextVersionInfo> getContentsThatPassedPredicateTestRows(IGUID context, boolean includeEvicted) {
        HashMap<IGUID, ContextVersionInfo> contents = mappings.get(context);
        if (contents == null) {
            return new HashMap<>();
        } else {
            return contents.entrySet()
                    .stream()
                    .filter(p -> p.getValue().predicateResult && (includeEvicted || !p.getValue().evicted))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        }
    }

    /**
     * Mark the version as evicted.
     * Evicted versions are not deleted, unless the LOCAL NODE FLUSHER really has to in order to make some space.
     *
     * @param context
     * @param version
     */
    @Override
    public void evict(IGUID context, IGUID version) {

        HashMap<IGUID, ContextVersionInfo> contents = mappings.get(context);
        if (contents != null) {

            ContextVersionInfo contextVersionInfo = contents.get(version);
            if (contextVersionInfo != null) {
                contextVersionInfo.evicted = true;
                contents.put(version, contextVersionInfo);
            }

        }
    }

    @Override
    public void delete(IGUID context, IGUID version) {

        if (mappings.containsKey(context)) {
            mappings.get(context).remove(version);
        }
    }

    @Override
    public void delete(IGUID context) {

        mappings.remove(context);
    }

    public void clear() {

        mappings.clear();
        lru.clear();
    }

    ///////////////////
    // Serialization //
    ///////////////////

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(mappings.size());

        // Store entries as ordered in the LRU
        ConcurrentLinkedQueue<IGUID> lruQueue = new  ConcurrentLinkedQueue<>(lru.getQueue());
        IGUID guid;
        while ((guid = lruQueue.poll()) != null) {
            out.writeUTF(guid.toMultiHash());

            HashMap<IGUID, ContextVersionInfo> values = mappings.get(guid);
            out.writeInt(values.size());
            for(Map.Entry<IGUID, ContextVersionInfo> content:values.entrySet()) {
                out.writeUTF(content.getKey().toMultiHash());
                out.writeBoolean(content.getValue().predicateResult);
                out.writeLong(content.getValue().timestamp.getEpochSecond());
                out.writeBoolean(content.getValue().policySatisfied);
                out.writeBoolean(content.getValue().evicted);
            }
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        try {
            lru = new LRU_GUID();

            mappings = new LinkedHashMap<>();

            int numberOfContexts = in.readInt();
            for (int i = 0; i < numberOfContexts; i++) {
                String guids = in.readUTF();
                IGUID contextGUID = GUIDFactory.recreateGUID(guids);

                mappings.put(contextGUID, new LinkedHashMap<>());

                int numberOfContents = in.readInt();
                for(int j = 0; j < numberOfContents; j++) {
                    String contentGUIDS = in.readUTF();
                    IGUID contentGUID = GUIDFactory.recreateGUID(contentGUIDS);
                    boolean predicateResult = in.readBoolean();
                    long timestamp = in.readLong();
                    boolean policySatisfied = in.readBoolean();
                    boolean evicted = in.readBoolean();

                    ContextVersionInfo contextVersionInfo = new ContextVersionInfo();
                    contextVersionInfo.predicateResult = predicateResult;
                    contextVersionInfo.timestamp = Instant.ofEpochSecond(timestamp);
                    contextVersionInfo.policySatisfied = policySatisfied;
                    contextVersionInfo.evicted = evicted;

                    mappings.get(contextGUID).put(contentGUID, contextVersionInfo);
                }
            }

        } catch (GUIDGenerationException e) {
            throw new IOException(e);
        }
    }
}
