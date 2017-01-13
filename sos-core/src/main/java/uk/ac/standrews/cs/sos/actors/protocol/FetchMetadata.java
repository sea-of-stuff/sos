package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchMetadata {

    /**
     * Fetch metadata that matches the metadataId from a specified node
     *
     * @param node - dds node where to fetch the metadata from
     * @param metadataId - guid of the metadata
     * @return SOSMetadata
     * @throws IOException
     * @throws SOSURLException
     */
    public static SOSMetadata Fetch(Node node, IGUID metadataId) throws IOException, SOSURLException {
        if (!node.isDDS()) {
            throw new IOException("Attempting to fetch metadata from non-DDS node");
        }

        if (metadataId == null || metadataId.isInvalid()) {
            throw new IOException("Attempting to fetch metadata, but you have given an invalid GUID");
        }

        SOS_LOG.log(LEVEL.INFO, "Metadata will be fetched from node " + node.getNodeGUID());

        URL url = SOSURL.DDS_GET_MANIFEST(node, metadataId);
        SyncRequest request = new SyncRequest(Method.GET, url);
        Response response = RequestsManager.getInstance().playSyncRequest(request);

        if (response.getCode() == HTTPStatus.OK) {
            SOS_LOG.log(LEVEL.INFO, "Metadata fetched successfully from node " + node.getNodeGUID());
        } else {
            SOS_LOG.log(LEVEL.WARN, "Metadata was not fetched successfully from node " + node.getNodeGUID());
        }

        // Get body and parse to metadata

        return null;
    }
}
