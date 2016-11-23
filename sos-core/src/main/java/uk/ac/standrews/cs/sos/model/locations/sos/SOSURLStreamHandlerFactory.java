package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.sos.interfaces.sos.NDS;
import uk.ac.standrews.cs.sos.storage.LocalStorage;

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
    protected static boolean URLStreamHandlerFactoryIsSet = false;

    private LocalStorage localStorage;
    private NDS nds;

    /**
     * Construct the factory with the given nodes directory.
     */
    public SOSURLStreamHandlerFactory(LocalStorage localStorage) {
        this.localStorage = localStorage;

        URLStreamHandlerFactoryIsSet = true;
    }

    protected void setNDS(NDS nds) {
        this.nds = nds;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equals(SOS_PROTOCOL_SCHEME)) {
            return new SOSURLStreamHandler(localStorage, nds);
        }

        return null;
    }

}
