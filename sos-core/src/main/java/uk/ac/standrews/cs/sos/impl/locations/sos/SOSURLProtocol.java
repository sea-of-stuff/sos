package uk.ac.standrews.cs.sos.impl.locations.sos;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandlerFactory;

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
            urlStreamHandlerFactory = new SOSURLStreamHandlerFactory(localStorage);

            forcefullyInstall(urlStreamHandlerFactory);
//            if(System.getProperty("uk.ac.standrews.cs.sos.streamHandlerFactoryInstalled") == null) {
//            // if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
//
//                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
//                System.setProperty("uk.ac.standrews.cs.sos.streamHandlerFactoryInstalled", "true");
//            }
        } catch (Error e) {
            SOS_LOG.log(LEVEL.WARN, "SOS Protocol registration failed: " + e.getMessage());
            throw new SOSProtocolException(e);
        }

        urlStreamHandlerFactory.getSOSURLStreamHandler().setNodeDiscoveryService(nodeDiscoveryService);
    }

    // Solution suggested here: https://stackoverflow.com/a/30524545/2467938
    public static void forcefullyInstall(URLStreamHandlerFactory factory) {
        try {
            // Try doing it the normal way
            URL.setURLStreamHandlerFactory(factory);
        } catch (final Error e) {
            // Force it via reflection
            try {
                final Field factoryField = URL.class.getDeclaredField("factory");
                factoryField.setAccessible(true);
                factoryField.set(null, factory);
            } catch (NoSuchFieldException | IllegalAccessException e1) {
                throw new Error("Could not access factory field on URL class: {}", e);
            }
        }
    }

}
