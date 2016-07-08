package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * This factory is used to add the sos:// scheme in the URL Stream Handler protocol.
 * All requests on sos:// will be handled by @see SOSURLConnection
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private final static String SOS_PROTOCOL_SCHEME = "sos";

    public static boolean URLStreamHandlerFactoryIsSet = false;

    private InternalStorage internalStorage;
    private NodeManager nodeManager;

    /**
     * Construct the factory with the given node manager.
     *
     * @param nodeManager
     */
    public SOSURLStreamHandlerFactory(InternalStorage internalStorage, NodeManager nodeManager) {
        this.internalStorage = internalStorage;
        this.nodeManager = nodeManager;
        URLStreamHandlerFactoryIsSet = true;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equals(SOS_PROTOCOL_SCHEME)) {
            return new SOSURLStreamHandler(internalStorage, nodeManager);
        }

        return null;
    }

}
