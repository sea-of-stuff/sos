package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.interfaces.model.PredicateComputationType;
import uk.ac.standrews.cs.sos.interfaces.model.Version;

import java.util.Iterator;

/**
 * Context Management Service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface CMS extends SeaOfStuff {

    /**
     *
     * The context is automatically set as active
     * @param context
     * @return
     * @throws Exception
     */
    // TODO - use context builder
    Version addContext(Context context) throws Exception;

    Context getContext(IGUID version);

    Iterator<IGUID> getContexts(PredicateComputationType type);

    Iterator<IGUID> getContents(IGUID context);

}
