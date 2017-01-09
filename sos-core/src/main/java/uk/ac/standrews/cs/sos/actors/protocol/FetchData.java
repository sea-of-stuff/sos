package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchData {

    /**
     * Fetch data that matched the entityId from a specified node
     *
     * @param node - storage node where to fetch the data from
     * @param entityId - guid of the data
     * @return InputStream of the data
     * @throws IOException
     * @throws SOSURLException
     */
    public static InputStream Fetch(Node node, IGUID entityId) throws IOException, SOSURLException {
        if (!node.isStorage()) {
            throw new IOException("Attempting to fetch data from non-Storage node");
        }

        if (entityId == null || entityId.isInvalid()) {
            throw new IOException("Attempting to fetch data, but you have given an invalid GUID");
        }

        SOS_LOG.log(LEVEL.INFO, "Data will be fetched from node " + node.getNodeGUID());

        URL url = SOSURL.STORAGE_GET_DATA(node, entityId);
        SyncRequest request = new SyncRequest(Method.GET, url);
        Response response = RequestsManager.getInstance().playSyncRequest(request);

        if (response.getCode() == HTTPStatus.OK) {
            SOS_LOG.log(LEVEL.INFO, "Data fetched successfully from node " + node.getNodeGUID());
        } else {
            SOS_LOG.log(LEVEL.WARN, "Data was not fetched successfully from node " + node.getNodeGUID());
        }

        return response.getBody();
    }
}
