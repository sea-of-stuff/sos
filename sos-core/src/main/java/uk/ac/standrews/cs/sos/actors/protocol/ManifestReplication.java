package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestReplication {

    public static ExecutorService Replicate(Manifest manifest, Set<Node> nodes) {

        ExecutorService executor = Executors.newCachedThreadPool();

        nodes.stream()
                .filter(Node::isDDS)
                .distinct()
                .forEach(n -> {
                    Runnable runnable = transferManifest(manifest, n);
                    executor.submit(runnable);
                });

        return executor;
    }

    private static Runnable transferManifest(Manifest manifest, Node node) {

        Runnable replicator = () -> {
            transferManifestRequest(manifest, node);
            // TODO - Collect information from requests and return it back
        };

        return replicator;
    }

    private static void transferManifestRequest(Manifest manifest, Node node) {
        System.out.println("Transfer manifest to DDS node - WORK IN PROGRESS");

        try {
            URL url = SOSEP.DDS_POST_MANIFEST(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setJSONBody(manifest.toString());

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            // JUST A 201 Response

//            InputStream body = response.getBody();
//            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);
//
//            System.out.println("RESPONSE from node: " + node.getNodeGUID().toString());
//            System.out.println(jsonNode.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
