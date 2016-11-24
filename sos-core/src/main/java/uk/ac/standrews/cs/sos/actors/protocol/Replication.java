package uk.ac.standrews.cs.sos.actors.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
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
    private static void transferData(InputStream data, Node node) {

        Runnable replicator = () -> {
            transferDataRequest(data, node);
            // TODO - Collect information from requests and return it back
        };

        replicator.run();
    }

    private static IGUID transferDataRequest(InputStream data, Node node) {

        IGUID retval = null;

        URL url;
        try {
            url = SOSEP.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setBody(data);

            Response response = RequestsManager.getInstance().playSyncRequest(request);

            InputStream body = response.getBody();
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);
            String guid = jsonNode.get("ContentGUID").textValue();

            // TODO - get locations back!
            retval = GUIDFactory.recreateGUID(guid);
        } catch (IOException | GUIDGenerationException e) {
            e.printStackTrace();
        }

        return retval;
    }

    public static void ReplicateManifest(Manifest manifest, Set<Node> nodes) {

        for(Node node:nodes) {
            transferManifest(manifest, node);
        }
    }

    private static void transferManifest(Manifest manifest, Node node) {
        
        Runnable replicator = () -> {
            transferManifestRequest(manifest, node);
            // TODO - Collect information from requests and return it back
        };

        replicator.run();
    }

    private static void transferManifestRequest(Manifest manifest, Node node) {
        System.out.println("Transfer manifest to DDS node - WORK IN PROGRESS");

        try {
            URL url = SOSEP.DDS_POST_MANIFEST(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setJSONBody(manifest.toString());

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            InputStream body = response.getBody();
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);

            System.out.println("RESPONSE from node: " + node.getNodeGUID().toString());
            System.out.println(jsonNode.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}