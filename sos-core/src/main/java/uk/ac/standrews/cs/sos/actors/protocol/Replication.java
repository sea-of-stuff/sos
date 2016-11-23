package uk.ac.standrews.cs.sos.actors.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Replication {

    public static void ReplicateData(InputStream data, Set<Node> nodes) {

        for(Node node:nodes) {
            transferData(data, node);
        }
    }

    // Synchronously
    private static void transferData(InputStream data, Node endpoint) {

        Runnable replicator = () -> {
            transferDataRequest(data, endpoint);
            // TODO - Collect information from requests and return it back
        };

        replicator.run();
    }

    private static IGUID transferDataRequest(InputStream data, Node endpoint) {

        IGUID retval = null;

        URL url;
        try {
            url = SOSEP.STORAGE_POST_DATA(endpoint);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setBody(data);

            Response response = RequestsManager.getInstance().playSyncRequest(request);

            JsonNode node = JSONHelper.JsonObjMapper().readTree(response.getBody());
            String guid = node.get("ContentGUID").textValue();

            // TODO - get locations back!
            retval = GUIDFactory.recreateGUID(guid);
        } catch (IOException | GUIDGenerationException e) {
            e.printStackTrace();
        }

        return retval;
    }

}