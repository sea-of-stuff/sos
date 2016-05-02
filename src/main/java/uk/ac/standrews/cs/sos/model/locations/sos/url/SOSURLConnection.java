package uk.ac.standrews.cs.sos.model.locations.sos.url;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedFile;
import uk.ac.standrews.cs.sos.network.Node;
import uk.ac.standrews.cs.sos.network.NodeManager;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLConnection extends URLConnection {

    private NodeManager nodeManager;

    /**
     * Constructs a URL connection to the specified URL. A connection to
     * the object referenced by the URL is not created.
     *
     * @param url the specified URL.
     */
    protected SOSURLConnection(NodeManager nodeManager, URL url) {
        super(url);
        this.nodeManager = nodeManager;
    }

    @Override
    public void connect() throws IOException {
        System.out.println("Connected!");
    }

    @Override
    public InputStream getInputStream() throws IOException {

        try {
            String[] segments = url.getPath().split("/");
            IGUID urlMachineId = GUIDFactory.recreateGUID(url.getHost());
            IGUID entityId = GUIDFactory.recreateGUID(segments[segments.length - 1]);
            IGUID thisMachineId = SeaConfiguration.getInstance().getNodeId();

            if (urlMachineId.equals(thisMachineId)) {
                SOSFile path = new FileBasedFile(SeaConfiguration.getInstance().getCacheDataPath(), entityId.toString());
                FileInputStream fileStream = new FileInputStream(path.getPathname());
                return new BufferedInputStream(fileStream);
            } else {
                /*
                 * TODO - get data from other nodes
                 * check NodeManager to see if node is known
                 *
                 */
                Node node = nodeManager.getNode(urlMachineId);
                InetSocketAddress inetSocketAddress = node.getHostAddress();
                String urlString = "http://" + inetSocketAddress.getHostName() +
                        ":" + inetSocketAddress.getPort() +
                        "/sos/find/manifest?guid=" + entityId.toString(); // TODO - this is hardcoded, plus this api end-point returns a manifest not data
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();

                return conn.getInputStream();
            }
        } catch (SeaConfigurationException | GUIDGenerationException e) {
            throw new IOException(); // FIXME - this try/catch is a bit dirty.
        }

        /*
         * lookup for node id in local map
         * otherwise contact registry/coordinator
         * talk to coordinator via http
         */
    }

}
