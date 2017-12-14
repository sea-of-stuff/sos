package uk.ac.standrews.cs.sos.impl.datamodel.locations.sos;

import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLStreamHandler extends URLStreamHandler {

    private LocalStorage localStorage;
    private NodeDiscoveryService nodeDiscoveryService;

    public SOSURLStreamHandler(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    protected URLConnection openConnection(URL url) {
        return new SOSURLConnection(localStorage, nodeDiscoveryService, url);
    }

    public void setNodeDiscoveryService(NodeDiscoveryService nodeDiscoveryService) {
        this.nodeDiscoveryService = nodeDiscoveryService;
    }
}
