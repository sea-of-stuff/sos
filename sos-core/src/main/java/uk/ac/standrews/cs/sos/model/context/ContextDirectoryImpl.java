package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.context.ContextDirectory;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Context;

import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO - add persistency
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextDirectoryImpl implements ContextDirectory {

    private HashMap<IGUID, Context> contexts;

    public ContextDirectoryImpl() {
        contexts = new HashMap<>();
    }

    @Override
    public void addContext(Context context) {
        contexts.put(context.getGUID(), context);
    }

    @Override
    public Context getContext(IGUID contextGUID) {
        return contexts.get(contextGUID);
    }

    @Override
    public void addToContext(IGUID contextGUID, Asset asset) {
        // TODO - run context against asset?
    }

    @Override
    public Iterator<IGUID> getFromContext(IGUID contextGUID) {
        return null;
    }
}
