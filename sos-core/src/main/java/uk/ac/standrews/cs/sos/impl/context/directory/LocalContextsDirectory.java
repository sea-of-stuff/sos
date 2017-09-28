package uk.ac.standrews.cs.sos.impl.context.directory;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.text.WordUtils;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.NameObjectBinding;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassLoaderException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.reflection.ClassLoader;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.impl.context.reflection.ContextClassBuilder.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalContextsDirectory {

    final private LocalStorage localStorage;
    final private PolicyActions policyActions;

    /**
     *
     * @param localStorage needed to interact with the node storage
     * @param policyActions this is needed to instantiate the context correctly when loaded from file
     */
    public LocalContextsDirectory(final LocalStorage localStorage, final PolicyActions policyActions) {
        this.localStorage = localStorage;
        this.policyActions = policyActions;
    }

    /**
     * Persist the context to the node storage
     *
     * @param context to be added
     * @return the guid of the context
     * @throws DataStorageException if the context could not be added
     */
    public IGUID addContext(Context context) throws DataStorageException {

        try {
            IDirectory contextsDirectory = localStorage.getContextsDirectory();
            IFile file = localStorage.createFile(contextsDirectory, context.guid().toMultiHash(), new StringData(context.toString()));
            file.persist();

            return context.guid();

        } catch (PersistenceException e) {
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
    public Context getContext(IGUID guid) throws ContextNotFoundException {

        try {
            IDirectory contextsDirectory = localStorage.getContextsDirectory();
            IFile file = localStorage.createFile(contextsDirectory, guid.toMultiHash());
            Data data = file.getData();

            // parse from json
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(data.toString());
            IGUID contextGUID = GUIDFactory.recreateGUID(jsonNode.get("guid").textValue());
            String contextName = WordUtils.capitalize(jsonNode.get(CONTEXT_JSON_NAME).textValue());
            NodesCollection domain = makeNodesCollection(jsonNode, CONTEXT_JSON_DOMAIN);
            NodesCollection codomain = makeNodesCollection(jsonNode, CONTEXT_JSON_CODOMAIN);

            ClassLoader.Load(jsonNode);
            Context context = ClassLoader.Instance(jsonNode, policyActions, contextGUID /* THIS IS IMPORTANT, AS WE ALREADY KNOW THE GUID OF THE CONTEXT */, contextName, domain, codomain);

            return context;

        } catch (DataStorageException | GUIDGenerationException | NodeNotFoundException | NodesCollectionException | ClassLoaderException | DataException | IOException e) {
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

        IDirectory contextsDirectory = localStorage.getContextsDirectory();
        for (Iterator<NameObjectBinding> it = contextsDirectory.getIterator(); it.hasNext(); ) {

            NameObjectBinding nob = it.next();
            IGUID guid = GUIDFactory.recreateGUID(nob.getName());
            contexts.add(guid);
        }

        return contexts;
    }
}
