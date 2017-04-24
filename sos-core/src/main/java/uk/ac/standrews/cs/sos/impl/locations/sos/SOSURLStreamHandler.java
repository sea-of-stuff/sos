package uk.ac.standrews.cs.sos.impl.locations.sos;

import uk.ac.standrews.cs.sos.actors.NDS;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandler extends URLStreamHandler {

    private LocalStorage localStorage;
    private NDS nds;

    public SOSURLStreamHandler(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new SOSURLConnection(localStorage, nds, url);
    }

    public void setNds(NDS nds) {
        this.nds = nds;
    }
}
