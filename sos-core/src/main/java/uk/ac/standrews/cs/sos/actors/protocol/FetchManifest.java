package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.manifests.directory.ManifestsUtils;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchManifest {

    /**
     * Fetch manifest that matches the manifestId from a specified node
     *
     * @param node - storage node where to fetch the data from
     * @param manifestId - guid of the data
     * @return InputStream of the data
     * @throws IOException
     * @throws SOSURLException
     */
    public static Manifest Fetch(Node node, IGUID manifestId) throws IOException, SOSURLException {
        if (!node.isDDS()) {
            throw new IOException("Attempting to fetch manifest from non-DDS node");
        }

        if (manifestId == null || manifestId.isInvalid()) {
            throw new IOException("Attempting to fetch manifest, but you have given an invalid GUID");
        }

        SOS_LOG.log(LEVEL.INFO, "Manifest will be fetched from node " + node.getNodeGUID());

        URL url = SOSURL.DDS_GET_MANIFEST(node, manifestId);
        SyncRequest request = new SyncRequest(Method.GET, url);
        Response response = RequestsManager.getInstance().playSyncRequest(request);

        if (response.getCode() == HTTPStatus.OK) {
            SOS_LOG.log(LEVEL.INFO, "Manifest fetched successfully from node " + node.getNodeGUID());
        } else {
            SOS_LOG.log(LEVEL.WARN, "Manifest was not fetched successfully from node " + node.getNodeGUID());
        }

        Manifest manifest;
        try(InputStream inputStream = response.getBody();) {
            String responseBody = IO.InputStreamToString(inputStream);
            manifest = ManifestsUtils.ManifestFromJson(responseBody);
        } catch (ManifestNotFoundException e) {
            throw new IOException("Unable to parse manifest with GUID " + manifestId);
        }

        return manifest;
    }
}
