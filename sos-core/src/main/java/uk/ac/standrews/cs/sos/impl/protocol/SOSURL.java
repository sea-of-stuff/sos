package uk.ac.standrews.cs.sos.impl.protocol;

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

    public static URL NODE_PAYLOAD(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "payload/";

        return makeURL(url);
    }

    public static URL NODE_INFO(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "info";

        return makeURL(url);
    }

    public static URL MDS_GET_MANIFEST(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "mds/manifest/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL MMS_GET_MANIFEST(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "mms/metadata/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL USRO_GET_MANIFEST(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "usro/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL CMS_GET_MANIFEST(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "cms/context/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL NDS_GET_MANIFEST(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "nds/node/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL MDS_GET_VERSIONS(Node node, IGUID invariant) throws SOSURLException {
        String url = buildURLBase(node) +
                "mds/versions/invariant/" + invariant.toMultiHash();

        return makeURL(url);
    }

    public static URL MDS_POST_MANIFEST(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "mds/manifest";

        return makeURL(url);
    }

    public static URL USRO_POST_USER_MANIFEST(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "usro/user";

        return makeURL(url);
    }

    public static URL USRO_POST_ROLE_MANIFEST(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "usro/role";

        return makeURL(url);
    }

    public static URL CMS_POST_MANIFEST(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "cms/context";

        return makeURL(url);
    }

    public static URL MMS_POST_MANIFEST(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "mms/metadata";

        return makeURL(url);
    }

    public static URL NDS_POST_MANIFEST(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "nds/node";

        return makeURL(url);
    }

    public static URL MDS_DELETE_MANIFEST(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "mds/manifest/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL CMS_DELETE_CONTEXT_VERSIONS(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "cms/context/invariant/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL STORAGE_GET_ATOM(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "storage/atom/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL STORAGE_POST_ATOM(Node node) throws SOSURLException {
        String url = buildURLBase(node) +
                "storage/atom";

        return makeURL(url);
    }

    public static URL STORAGE_ATOM_CHALLENGE(Node node, IGUID guid, String challenge) throws SOSURLException {
        String url = buildURLBase(node) +
                "storage/atom/atom/" + guid.toMultiHash() + "/challenge/" + challenge;

        return makeURL(url);
    }

    public static URL STORAGE_DELETE_ATOM(Node node, IGUID guid) throws SOSURLException {
        String url = buildURLBase(node) +
                "storage/atom/guid/" + guid.toMultiHash();

        return makeURL(url);
    }

    public static URL MDS_MANIFEST_CHALLENGE(Node node, IGUID guid, String challenge) throws SOSURLException {
        String url = buildURLBase(node) +
                "mds/manifest/guid/" + guid.toMultiHash() + "/challenge/" + challenge;

        return makeURL(url);
    }

    public static URL USRO_GET_ROLES(Node node, IGUID user) throws SOSURLException {

        String url = buildURLBase(node) +
                "usro/user/" + user.toMultiHash() + "/roles";

        return makeURL(url);
    }

    protected static URL makeURL(String urlString) throws SOSURLException {

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new SOSURLException();
        }

        return url;
    }

    protected static String buildURLBase(Node node) {
        String ip = node.getIP();
        InetSocketAddress address = node.getHostAddress();
        int port = address.getPort();
        return HTTP_SCHEME + ip + ":" + port + BASE_PATH;
    }
}
