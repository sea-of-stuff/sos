package uk.ac.standrews.cs.sos.node.directory;

import uk.ac.standrews.cs.IGUID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Indexing ManifestGUID --> [DDS]
 *
 * TODO - limit size?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DDSIndex {

    private HashMap<IGUID, Set<IGUID>> index;

    public DDSIndex() {
        index = new HashMap<>();
    }

    public void addEntry(IGUID manifestGUID, IGUID dds) {

        if (index.containsKey(manifestGUID)) {
            index.put(manifestGUID, new HashSet<>());
        }

        index.get(manifestGUID).add(dds);
    }

    public void evictEntry(IGUID manifestGUID, IGUID dds) {

        if (index.containsKey(manifestGUID)) {
            index.get(manifestGUID).remove(dds);
        }
    }

    public Set<IGUID> getDDSRefs(IGUID manifestGUID) {
        return index.get(manifestGUID);
    }
}
