package uk.ac.standrews.cs.sos.impl.locations.sos;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.FetchData;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class handles all requests on the URLs under the sos:// scheme.
 *
 * NOTE: what happens if two requests about the same data are made, but there are order issues?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLConnection extends URLConnection {

    private LocalStorage localStorage;
    private NodeDiscoveryService nodeDiscoveryService;

    /**
     * Constructs a URL connection to the specified URL. A connection to
     * the object referenced by the URL is not created.
     *
     * @param url the specified URL.
     */
    protected SOSURLConnection(LocalStorage localStorage, NodeDiscoveryService nodeDiscoveryService, URL url) {
        super(url);

        this.localStorage = localStorage;
        this.nodeDiscoveryService = nodeDiscoveryService;
    }

    @Override
    public void connect() throws IOException {}

    /**
     * Return the input stream given a sos location.
     *
     * @return data
     * @throws IOException if data could not be found
     */
    @Override
    public InputStream getInputStream() throws IOException {

        InputStream inputStream;

        try {
            IGUID nodeGUID = GUIDFactory.recreateGUID(url.getHost());
            IGUID entityGUID = GUIDFactory.recreateGUID(url.getFile().substring(1)); // skip initial slash

            if (isLocalNode(nodeGUID) && dataIsStoredLocally(entityGUID)) {
                inputStream = getDataLocally(entityGUID);
            } else {
                Node nodeToContact = nodeDiscoveryService.getNode(nodeGUID);

                FetchData fetchData = new FetchData(nodeToContact, entityGUID);
                TasksQueue.instance().performSyncTask(fetchData);
                inputStream = fetchData.getBody();
            }

        } catch (GUIDGenerationException | DataException
                | BindingAbsentException | DataStorageException
                | NodeNotFoundException e) {
            throw new IOException(e);
        }

        return inputStream;
    }

    private boolean isLocalNode(IGUID nodeGUID) {
        IGUID localNodeGUID = nodeDiscoveryService.getThisNode().getNodeGUID();
        return localNodeGUID.equals(nodeGUID);
    }

    private boolean dataIsStoredLocally(IGUID guid) throws DataStorageException {
        return localStorage.getDataDirectory().contains(guid.toString());
    }

    private InputStream getDataLocally(IGUID entityGUID) throws DataStorageException,
            BindingAbsentException, DataException, IOException {

        IDirectory directory = localStorage.getDataDirectory();
        String filename = entityGUID.toString();
        IFile file = (IFile) directory.get(filename);
        Data data = file.getData();

        return data.getInputStream();
    }

}
