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
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextLoader;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.impl.context.utils.ContextClassBuilder.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalContextsDirectory {

    final private LocalStorage localStorage;
    final private PolicyActions policyActions;

    public LocalContextsDirectory(final LocalStorage localStorage, final PolicyActions policyActions) {
        this.localStorage = localStorage;
        this.policyActions = policyActions;
    }

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

    public Context getContext(IGUID guid) throws ContextNotFoundException {

        try {
            IDirectory contextsDirectory = localStorage.getContextsDirectory();
            IFile file = localStorage.createFile(contextsDirectory, guid.toMultiHash());
            Data data = file.getData();

            // parse from json
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(new String(data.getState()));
            String contextName = WordUtils.capitalize(jsonNode.get(CONTEXT_JSON_NAME).textValue());
            NodesCollection domain = makeNodesCollection(jsonNode, CONTEXT_JSON_DOMAIN);
            NodesCollection codomain = makeNodesCollection(jsonNode, CONTEXT_JSON_CODOMAIN);

            ContextLoader.LoadContext(jsonNode);
            Context context = ContextLoader.Instance(contextName, jsonNode, policyActions, contextName, domain, codomain);

            return context;

        } catch (DataStorageException | GUIDGenerationException | NodeNotFoundException | NodesCollectionException | ContextLoaderException | DataException | IOException e) {
            throw new ContextNotFoundException(e);
        }
    }

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
