package uk.ac.standrews.cs.sos.impl.context.directory;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.model.ContextV;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * in-memory Directory of context definitions (not the contents)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheContextsDirectory {

    // GUID --> Context
    private transient HashMap<IGUID, ContextV> contexts;

    public CacheContextsDirectory() {
        contexts = new HashMap<>();
    }

    public IGUID addContext(ContextV context) {
        contexts.put(context.guid(), context);

        return context.guid();
    }

    public ContextV getContext(IGUID guid) throws ContextNotFoundException {

        if (contexts.containsKey(guid)) {
            return contexts.get(guid);
        }

        throw new ContextNotFoundException("No context with guid " + guid.toMultiHash() + " found in cache");
    }

    public Set<IGUID> getContexts() {
        return contexts.keySet();
    }

    public Set<ContextV> getContexts(String name) {

        Set<ContextV> ret = new LinkedHashSet<>();
        for(Map.Entry<IGUID, ContextV> e:contexts.entrySet()) {

            if (e.getValue().getName().toLowerCase().contains(name.toLowerCase())) {
                ret.add(e.getValue());
            }

        }

        return ret;
    }

}
