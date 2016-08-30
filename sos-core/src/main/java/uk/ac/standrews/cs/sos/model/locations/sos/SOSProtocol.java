package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.net.URL;
import java.net.URLStreamHandlerFactory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSProtocol {

    public static void Register(InternalStorage internalStorage, NodeManager nodeManager) throws SOSProtocolException {
        try {
            if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
                URLStreamHandlerFactory urlStreamHandlerFactory =
                        new SOSURLStreamHandlerFactory(internalStorage, nodeManager);
                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            }
        } catch (Error e) {
            throw new SOSProtocolException(e);
        }
    }
}
