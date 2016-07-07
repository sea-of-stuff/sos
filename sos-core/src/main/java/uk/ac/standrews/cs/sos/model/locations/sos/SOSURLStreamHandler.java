package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.sos.node.NodeManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandler extends URLStreamHandler {

    public NodeManager nodeManager;

    public SOSURLStreamHandler(NodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new SOSURLConnection(nodeManager, url);
    }
}
