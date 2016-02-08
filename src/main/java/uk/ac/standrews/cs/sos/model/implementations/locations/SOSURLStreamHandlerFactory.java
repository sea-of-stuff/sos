package uk.ac.standrews.cs.sos.model.implementations.locations;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandlerFactory implements URLStreamHandlerFactory {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("sos".equals(protocol)) {
            return new SOSURLStreamHandler();
        }

        return null;
    }

}
