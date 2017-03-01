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
    public Asset add(Context context) {
        contexts.put(context.getGUID(), context);
        return null;
    }

    @Override
    public Context get(IGUID version) {
        return null;
    }

    @Override
    public Asset update(IGUID guid, Context context) {
        return null;
    }

    @Override
    public Asset remove(IGUID guid) {
        return null;
    }

    @Override
    public void add(IGUID contextGUID, Asset asset) {

    }

    @Override
    public Iterator<IGUID> getContents(IGUID contextGUID) {
        return null;
    }
}
