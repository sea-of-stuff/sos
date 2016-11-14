package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.sos.node.NodesDirectory;
import uk.ac.standrews.cs.sos.storage.LocalStorage;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * This factory is used to add the sos:// scheme in the URL Stream Handler protocol.
 * All requests on sos:// will be handled by @see SOSURLConnection
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class    SOSURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private final static String SOS_PROTOCOL_SCHEME = "sos";

    public static boolean URLStreamHandlerFactoryIsSet = false;

    private LocalStorage localStorage;
    private NodesDirectory nodesDirectory;

    /**
     * Construct the factory with the given nodes directory.
     *
     * @param nodesDirectory
     */
    public SOSURLStreamHandlerFactory(LocalStorage localStorage, NodesDirectory nodesDirectory) {
        this.localStorage = localStorage;
        this.nodesDirectory = nodesDirectory;

        URLStreamHandlerFactoryIsSet = true;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equals(SOS_PROTOCOL_SCHEME)) {
            return new SOSURLStreamHandler(localStorage, nodesDirectory);
        }

        return null;
    }

}
