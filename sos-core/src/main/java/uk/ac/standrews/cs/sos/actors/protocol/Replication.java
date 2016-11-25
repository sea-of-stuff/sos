package uk.ac.standrews.cs.sos.actors.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.Tuple;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Replication {

    public static ExecutorService ReplicateData(InputStream data, Set<Node> nodes, LocationsIndex index) {

        ExecutorService executor = Executors.newCachedThreadPool();

        for(Node node:nodes) {
            Runnable runnable = transferData(data, node, index);

            executor.submit(runnable);
        }

        return executor;
    }

    // Synchronously
    private static Runnable transferData(InputStream data, Node node, LocationsIndex index) {

        Runnable replicator = () -> {
            Tuple<IGUID, Set<LocationBundle>> tuple = transferDataRequest(data, node);

            if (tuple == null) {
                // ERROR - Replication failed, should throw exception
                return;
            }

            for(LocationBundle locationBundle:tuple.y) {
                index.addLocation(tuple.x, locationBundle);
            }
        };

        return replicator;
    }

    private static Tuple<IGUID, Set<LocationBundle>> transferDataRequest(InputStream data, Node node) {

        Tuple<IGUID, Set<LocationBundle>> retval = null;

        URL url;
        try {
            url = SOSEP.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setBody(data);

            Response response = RequestsManager.getInstance().playSyncRequest(request);

            InputStream body = response.getBody();
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);
            String stringGUID = jsonNode.get("ContentGUID").textValue();

            IGUID guid = GUIDFactory.recreateGUID(stringGUID);

            JsonNode bundlesNode = jsonNode.get(ManifestConstants.KEY_LOCATIONS);
            Set<LocationBundle> bundles = new HashSet<>();
            if (bundlesNode.isArray()) {
                for(final JsonNode bundleNode:bundlesNode) {
                    LocationBundle bundle = JSONHelper.JsonObjMapper().convertValue(bundleNode, LocationBundle.class);
                    bundles.add(bundle);
                }
            }


            retval = new Tuple<>(guid, bundles);
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