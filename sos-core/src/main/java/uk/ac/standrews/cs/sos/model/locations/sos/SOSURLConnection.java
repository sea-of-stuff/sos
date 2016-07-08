package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.NodeManager;
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
    public void connect() throws IOException {
        System.out.println("Connected!");
    }

    @Override
    public InputStream getInputStream() throws IOException {

        try {

            IGUID thisGUID = nodeManager.getLocalNode().getNodeGUID();
            IGUID nodeGuid = GUIDFactory.recreateGUID(url.getHost());
            IGUID entityId = GUIDFactory.recreateGUID(url.getFile().substring(1)); // skip initial / sign

            if (thisGUID.equals(nodeGuid)) {

                Directory dataDir = internalStorage.getDataDirectory();

                File path = (File) dataDir.get(entityId.toString());
                return path.getData().getInputStream();
            } else {
                return null; // contactNode(resourceNode, entityId);
            }
        } catch ( GUIDGenerationException | DataException
                | BindingAbsentException e) {
            throw new IOException(e);
        }

        /*
         * lookup for node id in local map
         * otherwise contact registry/coordinator
         * talk to coordinator via http
         */
    }

    private InputStream contactNode(Node resourceNode, IGUID entityId) throws IOException {
        /*
         * TODO - get data from other nodes
         * check NodeManager to see if node is known
         *
         */
        Node nodeToContact = nodeManager.getNode(resourceNode.getNodeGUID()); // TODO - check if node exists!
        InetSocketAddress inetSocketAddress = nodeToContact.getHostAddress();
        String urlString = "http://" + inetSocketAddress.getHostName() +
                ":" + inetSocketAddress.getPort() +
                "/sos/find/manifest?guid=" + entityId.toString(); // TODO - this is hardcoded, plus this api end-point returns a manifest not data
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        return conn.getInputStream();
    }

}
