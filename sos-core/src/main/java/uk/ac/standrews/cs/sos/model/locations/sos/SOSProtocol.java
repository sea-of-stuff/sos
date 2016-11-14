package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.net.URL;
import java.net.URLStreamHandlerFactory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSProtocol {

    public static void Register(LocalStorage localStorage, NodeManager nodeManager) throws SOSProtocolException {
        SOS_LOG.log(LEVEL.INFO, "Registering the SOS Protocol");
        try {
            if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
                URLStreamHandlerFactory urlStreamHandlerFactory =
                        new SOSURLStreamHandlerFactory(localStorage, nodeManager);
                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            }
        } catch (Error e) {
            SOS_LOG.log(LEVEL.WARN, "SOS Protocol registration failed: " + e.getMessage());
            throw new SOSProtocolException(e);
        }
    }

}
