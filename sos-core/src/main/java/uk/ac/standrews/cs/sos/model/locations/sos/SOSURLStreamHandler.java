package uk.ac.standrews.cs.sos.model.locations.sos;

import uk.ac.standrews.cs.sos.node.NodesDirectory;
import uk.ac.standrews.cs.sos.storage.LocalStorage;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandler extends URLStreamHandler {

    private LocalStorage localStorage;
    private NodesDirectory nodesDirectory;

    public SOSURLStreamHandler(LocalStorage localStorage, NodesDirectory nodesDirectory) {
        this.localStorage = localStorage;
        this.nodesDirectory = nodesDirectory;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new SOSURLConnection(localStorage, nodesDirectory, url);
    }
}
