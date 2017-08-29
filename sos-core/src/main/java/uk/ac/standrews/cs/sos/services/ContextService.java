package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextContent;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.File;
import java.util.Queue;
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
     * Add a context from a file
     *
     * @param file
     * @return
     * @throws Exception
     */
    IGUID addContext(File file) throws Exception;

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
     */
    Set<Context> searchContexts(String contextName) throws ContextNotFoundException;

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

    /**
     * Run the predicates only against all the versions marked as HEADs in the node
     * @return the total number of times that any predicate has been run
     */
    int runPredicates();

    void runPolicies();

    Queue<Pair<Long, Long>> getPredicateThreadSessionStatistics();
    Queue<Pair<Long, Long>> getApplyPolicyThreadSessionStatistics();
    Queue<Pair<Long, Long>> getCheckPolicyThreadSessionStatistics();

    ContextContent getContextContentInfo(IGUID context, IGUID version);

}
