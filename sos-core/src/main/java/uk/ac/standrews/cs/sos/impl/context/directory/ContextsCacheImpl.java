package uk.ac.standrews.cs.sos.impl.context.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Scope;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextsCacheImpl {

    // GUID --> Context
    private transient HashMap<IGUID, Context> contexts;

    // GUID --> Scope
    private transient HashMap<IGUID, Scope> scopes;

    public ContextsCacheImpl() {
        contexts = new HashMap<>();
        scopes = new HashMap<>();
    }

    public void addContext(Context context) {
        contexts.put(context.guid(), context);
    }

    public Context getContext(IGUID guid) {
        return contexts.get(guid);
    }

    public Iterator<IGUID> getContexts() {
        return contexts.keySet().iterator();
    }

    public void addScope(Scope scope) {
        scopes.put(scope.guid(), scope);
    }

    public Scope getScope(IGUID guid) {
        return scopes.get(guid);
    }
}
