package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
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

    Context getContext(IGUID version) throws ContextNotFoundException;

    Iterator<IGUID> getContexts(PredicateComputationType type);

    /**
     * Run all predicates of contexts that match the specified computation type
     * The predicates will be run over the given version
     *
     * @param type
     * @param version
     */
    void runPredicates(PredicateComputationType type, Version version);

    Iterator<IGUID> getContents(IGUID context);

}
