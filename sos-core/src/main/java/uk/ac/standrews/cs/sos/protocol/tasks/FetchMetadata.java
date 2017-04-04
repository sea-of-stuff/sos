package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.model.Metadata;
import uk.ac.standrews.cs.sos.interfaces.model.Node;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.metadata.basic.BasicMetadata;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchMetadata extends Task {

    private Node node;
    private IGUID metadataId;
    private Metadata metadata;

    public FetchMetadata(Node node, IGUID metadataId) throws IOException {
        if (!node.isMMS()) {
            throw new IOException("Attempting to fetch metadata from non-MMS node");
        }

        if (metadataId == null || metadataId.isInvalid()) {
            throw new IOException("Attempting to fetch metadata, but you have given an invalid GUID");
        }

        this.node = node;
        this.metadataId = metadataId;
    }

    @Override
    public void performAction() {
        SOS_LOG.log(LEVEL.INFO, "Metadata will be fetched from node " + node.getNodeGUID());

        try {
            URL url = SOSURL.MMS_GET_METADATA(node, metadataId);
            SyncRequest request = new SyncRequest(Method.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {
                SOS_LOG.log(LEVEL.INFO, "Metadata fetched successfully from node " + node.getNodeGUID());

                try (InputStream inputStream = response.getBody()) {
                    this.metadata = JSONHelper.JsonObjMapper().readValue(inputStream, BasicMetadata.class);
                }

            } else {
                SOS_LOG.log(LEVEL.WARN, "Metadata was not fetched successfully from node " + node.getNodeGUID());
                throw new IOException();
            }
        } catch (IOException | SOSURLException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to fetch metadata");
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "FetchMetadata for guid " + metadataId + " from node " + node.getNodeGUID();
    }
}
