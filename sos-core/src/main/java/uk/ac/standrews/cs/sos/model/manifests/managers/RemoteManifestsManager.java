package uk.ac.standrews.cs.sos.model.manifests.managers;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.managers.ManifestsManager;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * The remote manifest manager allows the node to replicate manifests to other nodes in the SOS
 * as well as finding manifests in the rest of the SOS
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteManifestsManager implements ManifestsManager {

    private PolicyManager policyManager;
    private NodeManager nodeManager;
    private RequestsManager requestsManager;

    public RemoteManifestsManager(PolicyManager policyManager, NodeManager nodeManager, RequestsManager requestsManager) {
        this.policyManager = policyManager;
        this.nodeManager = nodeManager;
        this.requestsManager = requestsManager;
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {

        int replicationFactor = policyManager.getReplicationPolicy().getReplicationFactor();
        if (replicationFactor > 0) {

            Collection<Node> ddsNodes = nodeManager.getDDSNodes();

            for(Node ddsNode:ddsNodes) {
                addManifest(ddsNode, manifest);
            }

        }

        // check replication factor
        // find nodes
        // make requests
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {
        return null;
    }

    @Override
    public Stream<Manifest> getAllManifests() {
        return null;
    }

    private void addManifest(Node node, Manifest manifest) {

        try {
            URL url = SOSEP.DDS_POST_MANIFEST(node);

            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setJSONBody(manifest.toString());

            Response response = requestsManager.playSyncRequest(request);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
