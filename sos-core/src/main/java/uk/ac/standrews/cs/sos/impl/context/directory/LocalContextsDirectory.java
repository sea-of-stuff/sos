package uk.ac.standrews.cs.sos.impl.context.directory;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.model.ContextV;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalContextsDirectory {

    final private DataDiscoveryService dataDiscoveryService;
    final private PolicyActions policyActions;

    /**
     *
     * @param dataDiscoveryService needed to interact with the node first class entities
     * @param policyActions this is needed to instantiate the context correctly when loaded from file
     */
    public LocalContextsDirectory(final DataDiscoveryService dataDiscoveryService, final PolicyActions policyActions) {
        this.dataDiscoveryService = dataDiscoveryService;
        this.policyActions = policyActions;
    }

    /**
     * Persist the context to the node storage
     *
     * @param context to be added
     * @return the guid of the context
     * @throws DataStorageException if the context could not be added
     */
    public IGUID addContext(ContextV context) throws DataStorageException {

        try {
            dataDiscoveryService.addManifest(context);
            return context.guid();

        } catch (ManifestPersistException e) {
            throw new DataStorageException(e);
        }
    }

    /**
     * Get the context matching the guid from the node storage
     *
     * @param guid of the context
     * @return the instance of the context
     * @throws ContextNotFoundException if the context could not be found
     */
    public ContextV getContext(IGUID guid) throws ContextNotFoundException {

        try {
            ContextV context = (ContextV) dataDiscoveryService.getManifest(guid);

            // TODO - load the predicate and policies lazily
            // ClassLoader.Load(jsonNode);
            // Context context = ClassLoader.Instance(jsonNode, policyActions, guid /* THIS IS IMPORTANT, AS WE ALREADY KNOW THE GUID OF THE CONTEXT */, contextName, domain, codomain);

            return context;

        } catch (ManifestNotFoundException e) {
            throw new ContextNotFoundException(e);

        }
    }

    /**
     * Get the references to all the contexts stored in the node storage
     *
     * @return a set of references
     * @throws DataStorageException if unable to talk to the node storage
     * @throws GUIDGenerationException if one or more of the references are in a bad format
     */
    public Set<IGUID> getContexts() throws DataStorageException, GUIDGenerationException {

        Set<IGUID> contexts = new LinkedHashSet<>();

        // TODO - should have a lazy loading approach
//
//        IDirectory contextsDirectory = localStorage.getContextsDirectory();
//        for (Iterator<NameObjectBinding> it = contextsDirectory.getIterator(); it.hasNext(); ) {
//
//            NameObjectBinding nob = it.next();
//            IGUID guid = GUIDFactory.recreateGUID(nob.getName());
//            contexts.add(guid);
//        }

        return contexts;
    }
}
