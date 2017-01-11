package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * SOS URL End Points
 *
 * This is a helper class that should be used to get the SOS rest URL end-points
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURL {

    private static final String HTTP_SCHEME = "http://";

    public static URL DDS_POST_MANIFEST(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "/dds/manifest";

        return makeURL(url);
    }

    public static URL DDS_POST_METADATA(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "/dds/metadata";

        return makeURL(url);
    }

    public static URL STORAGE_GET_DATA(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "/storage/data/guid/" +
                guid.toString();

        return makeURL(url);
    }

    public static URL STORAGE_POST_DATA(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "/storage/stream";

        return makeURL(url);
    }

    public static URL NDS_REGISTER_NODE(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "/nds/register/";

        return makeURL(url);
    }

    public static URL NDS_GET_NODE(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "/nds/guid/" +
                guid.toString();

        return makeURL(url);
    }

    public static URL NDS_GET_NODE(Node node, String role) throws SOSURLException {
        String url = buildURLBase(node) +
                "/nds/role/" +
                role;

        return makeURL(url);
    }

    private static URL makeURL(String urlString) throws SOSURLException {

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new SOSURLException();
        }

        return url;
    }

    private static String buildURLBase(Node node) {
        InetSocketAddress address = node.getHostAddress();
        return HTTP_SCHEME + address.getHostName() + ":" + address.getPort();
    }
}
