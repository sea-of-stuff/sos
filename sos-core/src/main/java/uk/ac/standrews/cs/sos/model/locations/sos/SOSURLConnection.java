package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.protocol.SOSEP;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.storage.exceptions.DataException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class handles all requests on the URLs under the sos:// scheme.
 *
 * TODO: need to make sure that this code can work asynchrnously
 * NOTE: what happens if two requests about the same data are made, but there are order issues?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLConnection extends URLConnection {

    private LocalStorage localStorage;
    private NDS nds;

    /**
     * Constructs a URL connection to the specified URL. A connection to
     * the object referenced by the URL is not created.
     *
     * @param url the specified URL.
     */
    protected SOSURLConnection(LocalStorage localStorage,
                               NDS nds,
                               URL url) {
        super(url);

        this.localStorage = localStorage;
        this.nds = nds;
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

            boolean dataIsStoredLocally = dataIsStoredLocally(nodeGUID);
            if (dataIsStoredLocally) { // CASE 1
                inputStream = getDataLocally(entityGUID);
            } else {
                Node nodeToContact = nds.getNode(nodeGUID);
                inputStream = getDataFromNode(nodeToContact, entityGUID);
            }
        } catch (GUIDGenerationException | DataException
                | BindingAbsentException | DataStorageException
                | NodeNotFoundException e) {
            throw new IOException(e);
        }

        return inputStream;
    }

    private boolean dataIsStoredLocally(IGUID nodeGUID) {
        IGUID localNodeGUID = nds.getThisNode().getNodeGUID();
        return localNodeGUID.equals(nodeGUID);
    }

    private InputStream getDataLocally(IGUID entityGUID) throws DataStorageException,
            BindingAbsentException, DataException, IOException {
        SOS_LOG.log(LEVEL.INFO, "Data will be fetched from this node");

        Directory directory = localStorage.getDataDirectory();
        String filename = entityGUID.toString();
        File file = (File) directory.get(filename);
        Data data = file.getData();

        return data.getInputStream();
    }

    private InputStream getDataFromNode(Node node, IGUID entityId) throws IOException {
        SOS_LOG.log(LEVEL.INFO, "Data will be fetched from node " + node.getNodeGUID());

        URL url = SOSEP.STORAGE_GET_DATA(node, entityId);

        SyncRequest request = new SyncRequest(Method.GET, url);
        Response response = RequestsManager.getInstance().playSyncRequest(request);

        return response.getBody();
    }

}
