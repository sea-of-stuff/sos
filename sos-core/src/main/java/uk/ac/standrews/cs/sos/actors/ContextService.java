package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.model.Context;

import java.util.Set;

/**
 * Context Management Service
 *
 * On creation, the CMS will load the persisted contexts
 * Contexts will be referenced through versions
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ContextService {

    Set<Context> getContexts();

    /**
     * Adds a context to this service.
     * The context is automatically set as active
     *
     * @param context to be added
     * @throws Exception if the context could not be added
     */
    IGUID addContext(Context context) throws Exception;

    /**
     * Add a context to this service given a JSON representation of the context
     *
     * @param jsonContext
     * @return
     * @throws Exception
     */
    IGUID addContext(String jsonContext) throws Exception;

    /**
     * Get the context given its guid
     *
     * @return the matching context
     * @throws ContextNotFoundException if no context could be found
     */
    Context getContext(IGUID contextGUID) throws ContextNotFoundException;

    /**
     * Get a context given its name
     *
     * @param contextName
     * @return
     * @throws ContextNotFoundException
     *
     * TODO - assuming the the context name is unique
     */
    Context getContext(String contextName) throws ContextNotFoundException;

    /**
     * Get the set for all content belonging to the specified context
     *
     * @param context for which we want to find its contents
     * @return the references to the contents of the context
     *
     * TODO - only the refs of versions that are still tips?
     *
     */
    Set<IGUID> getContents(IGUID context);

    /**
     * Flushes any in-memory information into disk
     */
    void flush();

}
