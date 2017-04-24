package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.PredicateComputationType;
import uk.ac.standrews.cs.sos.model.Version;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

/**
 * Context Management Service
 *
 * On creation, the CMS will load the persisted contexts
 * Contexts will be referenced through versions
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface CMS extends SeaOfStuff {

    /**
     * Adds a context to this CMS and return a version for it.
     * The context is automatically set as active
     *
     * @param context
     * @return
     * @throws Exception
     */
    Version addContext(Context context) throws Exception;

    /**
     * Get the context for a specific version
     *
     * @param version
     * @return
     * @throws ContextNotFoundException
     */
    Context getContext(IGUID version) throws ContextNotFoundException;

    /**
     * Return all contexts that match the computation type specified
     *
     * @param type
     * @return
     */
    Iterator<IGUID> getContexts(PredicateComputationType type);

    /**
     * Instruct the CMS to map the given context with the specified version
     *
     * @param context
     * @param version
     */
    void addMapping(IGUID context, IGUID version);

    /**
     * Run all predicates of contexts that match the specified computation type
     * The predicates will be run over the given version
     *
     * @param type
     * @param version
     */
    Set<IGUID> runPredicates(PredicateComputationType type, Version version);

    /**
     *
     * @param type
     * @param data
     * @return the set of contexts that this version belongs to
     */
    Set<IGUID> runPredicates(PredicateComputationType type, InputStream data);

    /**
     * Get an iterator for all versions belonging to the specified context
     *
     * @param context
     * @return
     */
    Iterator<IGUID> getContents(IGUID context);

    /**
     * Flushes any in-memory information into disk
     */
    void flush();

}
