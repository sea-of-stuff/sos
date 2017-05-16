package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Scope;

import java.util.Iterator;

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
     * @param scope
     * @param context
     * @throws Exception
     */
    void addContext(Scope scope, Context context) throws Exception;

    /**
     * Get the context given its guid
     *
     * @return
     * @throws ContextNotFoundException
     */
    Context getContext(IGUID contextGUID) throws ContextNotFoundException;

    /**
     * Get an iterator for all content belonging to the specified context
     *
     * @param context
     * @return
     */
    Iterator<IGUID> getContents(IGUID context);

    /**
     * Add a scope to the CMS
     *
     * @param scope
     */
    void addScope(Scope scope);

    /**
     * Get the scope with the matching guid
     *
     * @param guid
     * @return
     */
    Scope getScope(IGUID guid);

    /**
     * Flushes any in-memory information into disk
     */
    void flush();

}
