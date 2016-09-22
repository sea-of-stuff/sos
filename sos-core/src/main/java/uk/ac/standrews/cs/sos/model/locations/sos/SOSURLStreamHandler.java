package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandler extends URLStreamHandler {

    private InternalStorage internalStorage;
    private NodeManager nodeManager;
    private RequestsManager requestsManager;

    public SOSURLStreamHandler(InternalStorage internalStorage, NodeManager nodeManager, RequestsManager requestsManager) {
        this.internalStorage = internalStorage;
        this.nodeManager = nodeManager;
        this.requestsManager = requestsManager;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new SOSURLConnection(internalStorage, nodeManager, requestsManager, url);
    }
}
