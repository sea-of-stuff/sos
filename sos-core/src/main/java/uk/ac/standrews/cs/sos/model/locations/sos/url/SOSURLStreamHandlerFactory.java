package uk.ac.standrews.cs.sos.model.locations.sos.url;

import uk.ac.standrews.cs.sos.node.SOSNodeManager;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandlerFactory implements URLStreamHandlerFactory {

    public static boolean URLStreamHandlerFactoryIsSet = false;

    private SOSNodeManager nodeManager;

    public SOSURLStreamHandlerFactory(SOSNodeManager nodeManager) {
        this.nodeManager = nodeManager;
        URLStreamHandlerFactoryIsSet = true;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("sos".equals(protocol)) {
            return new SOSURLStreamHandler(nodeManager);
        }

        return null;
    }

}
