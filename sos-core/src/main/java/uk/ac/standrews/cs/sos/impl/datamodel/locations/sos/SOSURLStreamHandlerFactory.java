package uk.ac.standrews.cs.sos.impl.datamodel.locations.sos;

import uk.ac.standrews.cs.sos.impl.node.LocalStorage;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import static uk.ac.standrews.cs.sos.constants.LocationSchemes.SOS_SCHEME;

/**
 * This factory is used to add the sos:// scheme in the URL Stream Handler protocol.
 * All requests on sos:// will be handled by @see SOSURLConnection
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private SOSURLStreamHandler sosurlStreamHandler;

    /**
     * Construct the factory with the given nodes directory.
     */
    public SOSURLStreamHandlerFactory(LocalStorage localStorage) {
        sosurlStreamHandler = new SOSURLStreamHandler(localStorage);
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equals(SOS_SCHEME)) {
            return sosurlStreamHandler;
        }

        return null;
    }

    protected SOSURLStreamHandler getSOSURLStreamHandler() {
        return sosurlStreamHandler;
    }

}
