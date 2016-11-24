package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
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

    public void register(LocalStorage localStorage) throws SOSProtocolException {
        SOS_LOG.log(LEVEL.INFO, "Registering the SOS Protocol");
        try {
            if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
                urlStreamHandlerFactory = new SOSURLStreamHandlerFactory(localStorage);

                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            }
        } catch (Error e) {
            SOS_LOG.log(LEVEL.WARN, "SOS Protocol registration failed: " + e.getMessage());
            throw new SOSProtocolException(e);
        }
    }

    public void setNDS(NDS nds) {
        urlStreamHandlerFactory.setNDS(nds);
    }

}
