package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.impl.context.ContextBuilder;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextVersionInfo;
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
public interface ContextService extends Service {

    /**
     * Get all the contexts run by this node
     *
     * @return set of context objects
     */
    Set<Context> getContexts();

    /**
     * Get all the contexts run by this node.
     * @return set of references to the contexts
     */
    Set<IGUID> getContextsRefs();

    /**
     * Adds a context to this service.
     * The context is automatically set as active
     *
     * @param context to be added
     * @return guid of added context
     * @throws ContextException if the context could not be added
     */
    IGUID addContext(Context context) throws ContextException;

    /**
     * Add a context to this service.
     *
     * @param contextBuilder containing the info to build the context
     * @return guid of added context
     * @throws ContextException if context could not be created/added
     */
    IGUID addContext(ContextBuilder contextBuilder) throws ContextException;

    /**
     * Add a context to this service given a JSON representation of the context
     *
     * @param jsonContext of the context to be added. Must be of FAT type.
     * @return guid of added context
     * @throws ContextException if context could not be created/added
     */
    IGUID addContext(String jsonContext) throws ContextException;

    /**
     * Add a context from a file
     *
     * @param file of context (in JSON)
     * @return guid of added context
     * @throws ContextException if context could not be created/added
     */
    IGUID addContext(File file) throws ContextException;

    /**
     *
     * @param prev context
     * @param contextBuilder should be of type TEMP (see constructors)
     * @return GUID of new context (or GUID of prev if context is not updated)
     * @throws ContextException if the context could not be updated
     */
    IGUID updateContext(Context prev, ContextBuilder contextBuilder) throws ContextException;

    /**
     * Get the context given its guid
     *
     * @return the matching context
     * @throws ContextNotFoundException if no context could be found
     */
    Context getContext(IGUID contextGUID) throws ContextNotFoundException;

    /**
     * Get the latest context version given the context invariant.
     * We have only TIPS for context versions. We do not have HEADS, as we do not have branching.
     *
     * @param invariant of the context versions
     * @return current TIP for the context identified by the invariant
     */
    Context getContextTIP(IGUID invariant) throws TIPNotFoundException, ContextNotFoundException;

    /**
     * Get a context given its name
     *
     * @param contextName string used for querying contexts
     * @return set of contexts matching the string name
     * @throws ContextNotFoundException if not context was found for given GUID
     */
    Set<Context> searchContexts(String contextName) throws ContextNotFoundException;

    /**
     * Get the set for all content belonging to the specified context.
     *
     * This method won't return contents which are evicted.
     *
     * @param context for which we want to find its contents
     * @return the references to the contents of the context
     *
     *
     */
    Set<IGUID> getContents(IGUID context);

    /**
     * Delete this context version and its results
     * @param context to be deleted
     * @throws ContextNotFoundException if the context could not be found and thus deleted
     */
    void deleteContextVersion(IGUID context) throws ContextNotFoundException;

    /**
     * Delete all the versions for this context and the associated results
     * @param invariant of context to be deleted
     * @throws ContextNotFoundException if one of the versions could not be found and thus deleted
     */
    void deleteContext(IGUID invariant) throws ContextNotFoundException;

    /**
     * Forces to run the predicate of the context with the matching GUID
     *
     * @param guid of context
     * @return number of assets processed
     * @throws ContextNotFoundException if not context was found for given GUID
     */
    int runContextPredicateNow(IGUID guid) throws ContextNotFoundException;

    /**
     * Forces to run the policies of the context with the matching GUID
     *
     * @param guid of context
     * @throws ContextNotFoundException if no context was found for given GUID
     */
    void runContextPolicyNow(IGUID guid) throws ContextNotFoundException;

    /**
     * Forces to run the check policies of the context with the matching GUID
     *
     * @param guid of context
     * @throws ContextNotFoundException if not context was found for given GUID
     */
    void runContextPolicyCheckNow(IGUID guid) throws ContextNotFoundException;

    /**
     * Run the predicates only against all the versions marked as HEADs in the node
     * @return the total number of times that any predicate has been run
     */
    int runPredicates() throws ContextException;

    /**
     * Run the apply policies functions for all contexts
     */
    void runPolicies();

    /**
     * Run the check policies functions for all contexts
     */
    void runCheckPolicies();

    // TODO - make sure that this call is correctly here
    void spawnContext(Context context) throws ManifestPersistException;

    /**
     * Get the statistics for the predicate thread
     * @return queue of <timestamp, duration> pairs
     */
    Queue<Pair<Long, Long>> getPredicateThreadSessionStatistics();

    /**
     * Get the statistics for the policy thread
     * @return queue of <timestamp, duration> pairs
     */
    Queue<Pair<Long, Long>> getApplyPolicyThreadSessionStatistics();

    /**
     * Get the statistics for the check-policy thread
     * @return queue of <timestamp, duration> pairs
     */
    Queue<Pair<Long, Long>> getCheckPolicyThreadSessionStatistics();

    /**
     * Get the info about the pair context-version
     * @param context guid
     * @param version guid
     * @return info about version mapped at given context
     */
    ContextVersionInfo getContextContentInfo(IGUID context, IGUID version);

}
