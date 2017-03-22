package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.interfaces.model.Version;

import java.util.Iterator;

/**
 * Context Management Service
 *
 * TODO - have methods to set active/inactive contexts
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface CMS extends SeaOfStuff {

    Version addContext(Context context) throws Exception;

    Context getContext(IGUID version);

    Version update(IGUID version, Context context);

    Version remove(IGUID guid);

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
