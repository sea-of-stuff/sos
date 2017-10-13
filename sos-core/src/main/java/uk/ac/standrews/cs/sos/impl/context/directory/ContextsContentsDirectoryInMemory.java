package uk.ac.standrews.cs.sos.impl.context.directory;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.context.ContextsContentsDirectory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * The ContextsDirectory holds all the information regarding contexts and their contents.
 * The actual context definitions are stored using the LocalContextsDirectroy and the CacheContextsDirectory
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextsContentsDirectoryInMemory implements Serializable, ContextsContentsDirectory {

    // Maps the context to the versions belonging to it
    // [ context -> [version, ContextVersionInfo] ]
    private transient HashMap<IGUID, HashMap<IGUID, ContextVersionInfo>> mappings;
    
    public ContextsContentsDirectoryInMemory() {
        mappings = new HashMap<>();
    }

    @Override
    public void addEntry(IGUID context, IGUID version, ContextVersionInfo content) {

        if (!mappings.containsKey(context)) {
            mappings.put(context, new HashMap<>());
        }

        mappings.get(context).put(version, content);
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

        if (entryExists(context, version)) {
            return mappings.get(context).get(version);
        } else {
            return new ContextVersionInfo();
        }
    }

    public void remove(IGUID context, IGUID version) {

        if (entryExists(context, version)) {
            mappings.get(context).remove(version);
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

    /**
     * Get a set for all the contents for a given context
     *
     * @param context
     * @return
     */
    @Override
    public Set<IGUID> getVersionsThatPassedPredicateTest(IGUID context, boolean includeEvicted) {
        HashMap<IGUID, ContextVersionInfo> contents = mappings.get(context);
        if (contents == null) {
            return Collections.emptySet();
        } else {
            return contents.entrySet()
                    .stream()
                    .filter(p -> p.getValue().predicateResult && (includeEvicted || !p.getValue().evicted))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

        }
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

    ///////////////////
    // Serialization //
    ///////////////////

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(mappings.size());
        for(Map.Entry<IGUID, HashMap<IGUID, ContextVersionInfo>> mapping:mappings.entrySet()) {
            out.writeUTF(mapping.getKey().toMultiHash());

            out.writeInt(mapping.getValue().size());
            for(Map.Entry<IGUID, ContextVersionInfo> content:mapping.getValue().entrySet()) {
                out.writeUTF(content.getKey().toMultiHash());
                out.writeBoolean(content.getValue().predicateResult);
                out.writeLong(content.getValue().timestamp);
                out.writeBoolean(content.getValue().policySatisfied);
                out.writeBoolean(content.getValue().evicted);
            }
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        try {
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
                    contextVersionInfo.timestamp = timestamp;
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
