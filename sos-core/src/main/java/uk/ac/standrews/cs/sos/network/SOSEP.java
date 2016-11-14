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
 * TODO - SOSURLException
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSEP {

    private static final String HTTP_SCHEME = "http://";

    public static URL DDS_POST_MANIFEST(Node node) throws MalformedURLException {
        String url = buildURLBase(node) +
                "/dds/manifest";

        return new URL(url);
    }

    public static URL STORAGE_GET_DATA(Node node, IGUID guid) throws MalformedURLException {
        String url = buildURLBase(node) +
                "/storage/data/guid/" +
                guid.toString();

        return new URL(url);
    }

    public static URL STORAGE_POST_DATA(Node node) throws MalformedURLException {
        String url = buildURLBase(node) +
                "/storage/stream";

        return new URL(url);
    }

    public static URL NDS_REGISTER_NODE(Node node, IGUID guid) throws MalformedURLException {
        String url = buildURLBase(node) +
                "/nds/register/";

        return new URL(url);
    }

    public static URL NDS_GET_NODE(Node node, IGUID guid) throws MalformedURLException {
        String url = buildURLBase(node) +
                "/nds/guid/" +
                guid.toString();

        return new URL(url);
    }

    public static URL NDS_GET_NODE(Node node, String role) throws MalformedURLException {
        String url = buildURLBase(node) +
                "/nds/role/" +
                role;

        return new URL(url);
    }

    private static String buildURLBase(Node node) {
        InetSocketAddress address = node.getHostAddress();
        String baseURL = HTTP_SCHEME + address.getHostName() + ":" + address.getPort();

        return baseURL;
    }
}
