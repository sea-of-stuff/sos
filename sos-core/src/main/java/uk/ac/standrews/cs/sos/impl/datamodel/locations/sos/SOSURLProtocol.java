package uk.ac.standrews.cs.sos.impl.datamodel.locations.sos;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.net.URL;

/**
 * Singleton class
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLProtocol {

    private static SOSURLProtocol instance;
    private SOSURLStreamHandlerFactory urlStreamHandlerFactory;

    private SOSURLProtocol() {}

    public static SOSURLProtocol getInstance() {
        if (instance == null) {
            instance = new SOSURLProtocol();
        }
        return instance;
    }

    public void register(LocalStorage localStorage, NodeDiscoveryService nodeDiscoveryService) throws SOSProtocolException {
        SOS_LOG.log(LEVEL.INFO, "Registering the SOS Protocol");
        try {

            // Solution suggested here: https://stackoverflow.com/a/30524545/2467938
            if (System.getProperty("uk.ac.standrews.cs.sos.streamHandlerFactoryInstalled") == null ||
                    System.getProperty("uk.ac.standrews.cs.sos.streamHandlerFactoryInstalled").equalsIgnoreCase("false")) {

                urlStreamHandlerFactory = new SOSURLStreamHandlerFactory(localStorage);

                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
                System.setProperty("uk.ac.standrews.cs.sos.streamHandlerFactoryInstalled", "true");
            }

        } catch (final Error e) {
            SOS_LOG.log(LEVEL.WARN, "SOS Protocol registration failed: " + e.getMessage());
            throw new SOSProtocolException(e);
        }

        if (urlStreamHandlerFactory != null) {
            SOSURLStreamHandler handler = urlStreamHandlerFactory.getSOSURLStreamHandler();
            handler.setNodeDiscoveryService(nodeDiscoveryService);
        }
    }

}
