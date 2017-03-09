package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Context;

import java.util.Iterator;

/**
 * Context Management Service
 *
 * TODO - have methods to set active/inactive contexts
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface CMS extends SeaOfStuff {

    Asset addContext(Context context) throws Exception;

    Context getContext(IGUID version);

    Asset update(IGUID version, Context context);

    Asset remove(IGUID guid);

    Iterator<IGUID> getContents(IGUID version);

    /**
     * Verify if the given version should belong to the specified context
     * @param context
     * @param version
     * @return
     */
    boolean verify(IGUID context, IGUID version);

    void getActiveContexts();
    void setContext(Context context, boolean active);

    /**
     * Run the all active contexts against the available assets
     * This method should be run periodically to avoid spending too much time on it
     */
    void process();
}
