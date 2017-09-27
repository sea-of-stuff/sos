package uk.ac.standrews.cs.sos.protocol;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.model.Node;

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
    private static final String BASE_PATH = "/sos/";

    public static URL NODE_PING(Node node, String message) throws SOSURLException {
        String url = buildURLBase(node) +
                "ping/" + message;

        return makeURL(url);
    }

    public static URL NODE_INFO(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "info";

        return makeURL(url);
    }

    public static URL DDS_GET_MANIFEST(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "dds/manifest/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL DDS_GET_VERSIONS(Node node, IGUID invariant) throws SOSURLException {
        String url = buildURLBase(node) +
                "dds/versions/invariant/" + invariant.toMultiHash();

        return makeURL(url);
    }

    public static URL MMS_GET_METADATA(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "mms/metadata/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL DDS_POST_MANIFEST(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "dds/manifest";

        return makeURL(url);
    }

    public static URL MMS_POST_METADATA(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "mms/metadata";

        return makeURL(url);
    }

    public static URL STORAGE_GET_DATA(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "storage/data/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL STORAGE_POST_DATA(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "storage/stream";

        return makeURL(url);
    }

    public static URL STORAGE_POST_REPLICATE_DATA(Node node, int replicas) throws SOSURLException {
        String url = buildURLBase(node) +
                "storage/stream/replicas/" + replicas;

        return makeURL(url);
    }

    public static URL STORAGE_DATA_CHALLENGE(Node node, IGUID guid, String challenge) throws SOSURLException {
        String url = buildURLBase(node) +
                "storage/data/guid/" + guid.toMultiHash() + "/challenge/" + challenge;

        return makeURL(url);
    }

    public static URL DDS_MANIFEST_CHALLENGE(Node node, IGUID guid, String challenge) throws SOSURLException {
        String url = buildURLBase(node) +
                "dds/manifest/guid/" + guid.toMultiHash() + "/challenge/" + challenge;

        return makeURL(url);
    }

    public static URL NDS_REGISTER_NODE(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "nds/register";

        return makeURL(url);
    }

    public static URL NDS_GET_NODE(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "nds/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL NDS_GET_NODE(Node node, String role) throws SOSURLException {
        String url = buildURLBase(node) +
                "nds/service/" + role;

        return makeURL(url);
    }

    public static URL RMS_GET_USER(Node node, IGUID user) throws SOSURLException {

        String url = buildURLBase(node) +
                "usro/user/" + user.toMultiHash();

        return makeURL(url);
    }

    public static URL RMS_GET_ROLE(Node node, IGUID role) throws SOSURLException {

        String url = buildURLBase(node) +
                "usro/role/" + role.toMultiHash();

        return makeURL(url);
    }

    public static URL RMS_GET_ROLES(Node node, IGUID user) throws SOSURLException {

        String url = buildURLBase(node) +
                "usro/user/" + user.toMultiHash() + "/roles";

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
        String ip = node.getHostname();
        InetSocketAddress address = node.getHostAddress();
        return HTTP_SCHEME + ip + ":" + address.getPort() + BASE_PATH;
    }
}
