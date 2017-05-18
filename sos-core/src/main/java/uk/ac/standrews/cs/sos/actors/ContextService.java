package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.model.Context;

import java.util.Iterator;

/**
 * Context Management Service
 *
 * On creation, the CMS will load the persisted contexts
 * Contexts will be referenced through versions
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ContextService extends SeaOfStuff {

    /**
     * Adds a context to this CMS and return a version for it.
     * The context is automatically set as active
     *
     * @param context
     * @throws Exception
     */
    void addContext(Context context) throws Exception;

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
     * Flushes any in-memory information into disk
     */
    void flush();

}
