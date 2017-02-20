package uk.ac.standrews.cs.sos.actors.protocol.tasks;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.actors.protocol.SOSURL;
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
import uk.ac.standrews.cs.sos.tasks.Task;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchManifest extends Task {

    private Node node;
    private IGUID manifestId;
    private Manifest manifest;

    public FetchManifest(Node node, IGUID manifestId) throws IOException {
        if (!node.isDDS()) {
            throw new IOException("Attempting to fetch manifest from non-DDS node");
        }

        if (manifestId == null || manifestId.isInvalid()) {
            throw new IOException("Attempting to fetch manifest, but you have given an invalid GUID");
        }

        this.node = node;
        this.manifestId = manifestId;
    }

    @Override
    public void performAction() {

        SOS_LOG.log(LEVEL.INFO, "Manifest will be fetched from node " + node.getNodeGUID());

        try {
            URL url = SOSURL.DDS_GET_MANIFEST(node, manifestId);
            SyncRequest request = new SyncRequest(Method.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {
                SOS_LOG.log(LEVEL.INFO, "Manifest fetched successfully from node " + node.getNodeGUID());

                try (InputStream inputStream = response.getBody()) {
                    String responseBody = IO.InputStreamToString(inputStream);
                    this.manifest = ManifestsUtils.ManifestFromJson(responseBody);
                } catch (ManifestNotFoundException e) {
                    throw new IOException("Unable to parse manifest with GUID " + manifestId);
                }

            } else {
                SOS_LOG.log(LEVEL.WARN, "Manifest was not fetched successfully from node " + node.getNodeGUID());
                throw new IOException();
            }
        } catch (SOSURLException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to fetch manifest");
        }
    }

    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public String toString() {
        return "FetchManifest for guid " + manifestId + " from node " + node.toString();
    }
}
