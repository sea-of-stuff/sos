package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.storage.exceptions.DataException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class handles all requests on the URLs under the sos:// scheme.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLConnection extends URLConnection {

    private InternalStorage internalStorage;
    private NodeManager nodeManager;

    /**
     * Constructs a URL connection to the specified URL. A connection to
     * the object referenced by the URL is not created.
     *
     * @param url the specified URL.
     */
    protected SOSURLConnection(InternalStorage internalStorage,
                               NodeManager nodeManager, URL url) {
        super(url);

        this.internalStorage = internalStorage;
        this.nodeManager = nodeManager;
    }

    @Override
    public void connect() throws IOException {}

    /**
     * Return the input stream given a sos location.
     * There are two cases:
     * 1 - the location is this one, thus we get the data from the internal storage
     * 2 - the location is not this node:
     *  a - if the location is known, we contact that node.
     *  b - if the location is not known, we contact a nds first
     *
     * @return
     * @throws IOException
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
                Node nodeToContact = nodeManager.getNode(nodeGUID);

                if (nodeToContact != null) { // CASE 2.A
                    inputStream = contactNode(nodeToContact, entityGUID);
                } else { // CASE 2.B
                    // TODO - contact nds, get info about node and then contact node

                    // Contact known NDS

                    inputStream = null;
                }
            }
        } catch (GUIDGenerationException | DataException
                | BindingAbsentException | DataStorageException e) {
            throw new IOException(e);
        }

        return inputStream;
    }

    private boolean dataIsStoredLocally(IGUID nodeGUID) {
        IGUID localNodeGUID = nodeManager.getLocalNode().getNodeGUID();
        return localNodeGUID.equals(nodeGUID);
    }

    private InputStream getDataLocally(IGUID entityGUID) throws DataStorageException,
            BindingAbsentException, DataException, IOException {

        Directory directory = internalStorage.getDataDirectory();
        String filename = entityGUID.toString();
        File file = (File) directory.get(filename);
        Data data = file.getData();

        return data.getInputStream();
    }

    private InputStream contactNode(Node node, IGUID entityId) throws IOException {

        InetSocketAddress inetSocketAddress = node.getHostAddress();
        String urlString = storageHTTPEndPoint(inetSocketAddress, entityId);

        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        return connection.getInputStream();
    }

    private String storageHTTPEndPoint(InetSocketAddress address, IGUID guid) {
        String url = "http://" +
                address.getHostName() +
                ":" + address.getPort() +
                "/storage/data?guid=" +
                guid.toString();

        return url;
    }

}
