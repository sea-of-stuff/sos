package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.HTTPMethod;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.FileUtils;
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

        if (manifestId == null || manifestId.isInvalid()) {
            throw new IOException("Attempting to fetch manifest, but you have given an invalid GUID");
        }

        this.node = node;
        this.manifestId = manifestId;
    }

    @Override
    public void performAction() {

        SOS_LOG.log(LEVEL.INFO, "Manifest with GUID " + manifestId.toMultiHash() + " will be fetched from node " + node.guid().toShortString());

        try {
            URL url = getManifestURL(node, manifestId);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {

                try (InputStream inputStream = response.getBody()) {

                    String responseBody = IO.InputStreamToString(inputStream);
                    this.manifest = FileUtils.ManifestFromJson(responseBody);
                    SOS_LOG.log(LEVEL.INFO, "Manifest fetched successfully from node " + node.guid());

                } catch (ManifestNotFoundException e) {
                    throw new IOException("Unable to parse manifest with GUID " + manifestId);
                }

            } else {
                SOS_LOG.log(LEVEL.ERROR, "Manifest was not fetched successfully from node " + node.guid().toShortString());
                throw new IOException();
            }
        } catch (SOSURLException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to fetch manifest");
        }
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Task deserialize(String json) throws IOException {
        return null;
    }

    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public String toString() {
        return "FetchManifest for guid " + manifestId + " from node " + node.guid();
    }

    private URL getManifestURL(Node node, IGUID manifestId) throws SOSURLException {

        if (node.isDDS()) {
            return SOSURL.DDS_GET_MANIFEST(node, manifestId);

        } else if (node.isRMS()) {
            return SOSURL.USRO_GET_MANIFEST(node, manifestId);

        } else if (node.isCMS()) {
            return SOSURL.CMS_GET_MANIFEST(node, manifestId);

        } else if (node.isMMS()) {
            return SOSURL.MMS_GET_MANIFEST(node, manifestId);

        } else if (node.isNDS()) {
            return SOSURL.NDS_GET_MANIFEST(node, manifestId);
        }

        throw new SOSURLException("Unable to return manifest URL for node " + node.toString());

    }
}
