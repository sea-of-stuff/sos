package uk.ac.standrews.cs.sos.model.locations.sos.url;

import uk.ac.standrews.cs.sos.network.NodeManager;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private NodeManager nodeManager;

    public SOSURLStreamHandlerFactory(NodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("sos".equals(protocol)) {
            return new SOSURLStreamHandler(nodeManager);
        }

        return null;
    }

}
