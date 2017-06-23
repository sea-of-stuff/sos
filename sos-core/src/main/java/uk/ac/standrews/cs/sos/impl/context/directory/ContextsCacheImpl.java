package uk.ac.standrews.cs.sos.impl.context.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.Context;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextsCacheImpl {

    // GUID --> Context
    private transient HashMap<IGUID, Context> contexts;

    public ContextsCacheImpl() {
        contexts = new HashMap<>();
    }

    public IGUID addContext(Context context) {
        contexts.put(context.guid(), context);

        return context.guid();
    }

    public Context getContext(IGUID guid) {
        return contexts.get(guid);
    }

    public Set<IGUID> getContexts() {
        return contexts.keySet();
    }

}
