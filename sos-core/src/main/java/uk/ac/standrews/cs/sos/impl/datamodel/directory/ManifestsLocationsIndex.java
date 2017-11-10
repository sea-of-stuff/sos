package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * The DDSIndex maps the GUID for a manifest to a set of DDS GUIDs that may have it
 *
 * TODO - limit size, but should always have at least N entries of one kind (e.g. dds, nds, etc)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsLocationsIndex implements Serializable {

    private transient HashMap<IGUID, Set<IGUID>> index;

    public ManifestsLocationsIndex() {
        index = new HashMap<>();
    }

    public void addEntry(IGUID manifestGUID, IGUID dds) {

        if (!index.containsKey(manifestGUID)) {
            index.put(manifestGUID, new HashSet<>());
        }

        index.get(manifestGUID).add(dds);
    }

    public void evictEntry(IGUID manifestGUID, IGUID dds) {

        if (index.containsKey(manifestGUID)) {
            index.get(manifestGUID).remove(dds);
        }
    }

    public Set<IGUID> getNodeRefs(IGUID manifestGUID) {

        if (index.containsKey(manifestGUID)) {
            return index.get(manifestGUID);
        } else {
            return new LinkedHashSet<>();
        }
    }

    public void clear() {
        index = new HashMap<>();
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(index.size());
        for (Map.Entry<IGUID, Set<IGUID>> entry : index.entrySet()) {
            IGUID key = entry.getKey();
            Set<IGUID> values = entry.getValue();

            out.writeUTF(key.toMultiHash());
            out.writeInt(values.size());

            for(IGUID value:values) {
                out.writeUTF(value.toMultiHash());
            }
        }

    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

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
