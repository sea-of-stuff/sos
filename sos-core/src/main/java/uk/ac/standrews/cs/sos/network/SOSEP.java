package uk.ac.standrews.cs.sos.network;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * SOS End Points
 *
 * This is a helper class that should be used to get the SOS rest URL end-points
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSEP {

    private static final String HTTP_SCHEME = "http://";

    public static URL DDS_MANIFEST(Node node) throws MalformedURLException {
        InetSocketAddress address = node.getHostAddress();
        String url = HTTP_SCHEME +
                address.getHostName() +
                ":" + address.getPort() +
                "/dds/manifest";

        return new URL(url);
    }

    public static URL STORAGE_GET_DATA(Node node, IGUID guid) throws MalformedURLException {
        InetSocketAddress address = node.getHostAddress();
        String url = HTTP_SCHEME +
                address.getHostName() +
                ":" + address.getPort() +
                "/storage/data/guid/" +
                guid.toString();

        return new URL(url);
    }
}
