package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The ManifestReplication task, as the name suggests, replicates a manifest to other nodes.
 * The manifest can be replicated only to DataDiscoveryServices (DDS).
 * In doing the replication the caller MUST also specify a wished replication factor for the manifest.
 *
 * If the manifest is successfully replicated to a DDS node:
 * - the local DDS is informed that now that such a manifest is now stored in that node too
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestReplication extends Task {

    private Manifest manifest;
    private NodesCollection nodesCollection;
    private int replicationFactor;
    private NodeDiscoveryService nodeDiscoveryService;
    private ManifestsDataService manifestsDataService;

    // TODO - replication by delegation. See DataReplication!
    public ManifestReplication(Manifest manifest, NodesCollection nodesCollection, int replicationFactor,
                               NodeDiscoveryService nodeDiscoveryService, ManifestsDataService manifestsDataService) throws SOSProtocolException {
        super();

        if (manifestsDataService == null || nodeDiscoveryService == null) {
            setState(TaskState.ERROR);
            throw new SOSProtocolException("At least one of the SOS services is null. Manifest replication process is aborted.");
        }

        this.nodeDiscoveryService = nodeDiscoveryService;
        this.manifestsDataService = manifestsDataService;

        this.manifest = manifest;
        this.nodesCollection = nodesCollection;
        this.replicationFactor = replicationFactor;
    }

    @Override
    public void performAction() {

        int successfulReplicas = 0;
        Iterator<IGUID> nodeRefs = nodesCollection.nodesRefs().iterator();
        while(nodeRefs.hasNext() && successfulReplicas < replicationFactor) {

            IGUID ref = nodeRefs.next();
            try {
                Node node = nodeDiscoveryService.getNode(ref);
                boolean transferWasSuccessful = transferManifestRequest(manifest, node);

                if (transferWasSuccessful) {
                    SOS_LOG.log(LEVEL.INFO, "Manifest with GUID " + manifest.guid() + " replicated successfully to node: " + node.guid().toMultiHash());
                    manifestsDataService.addManifestNodeMapping(manifest.guid(), ref);
                    successfulReplicas++;
                } else {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to replicate Manifest with GUID " + manifest.guid() + " to node: " + node.guid().toMultiHash());
                }

            } catch (NodeNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to perform replication at node with ref: " + ref);
            }

        }

        if (successfulReplicas >= replicationFactor) {
            setState(TaskState.SUCCESSFUL);
        } else {
            setState(TaskState.UNSUCCESSFUL);
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

    @Override
    public String toString() {
        return "ManifestReplication. ReplicationFactor: " + replicationFactor;
    }

    private boolean transferManifestRequest(Manifest manifest, Node node) {

        try {
            URL url = getManifestURL(node, manifest.getType());
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.POST, url, ResponseType.JSON);
            String manifestToSend = manifestToSend(manifest);
            request.setJSONBody(manifestToSend);

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            if (response instanceof ErrorResponseImpl) {
                setState(TaskState.ERROR);
                throw new IOException();
            }

            boolean transferWasSuccessful = response.getCode() == HTTPStatus.CREATED;
            try(InputStream ignored = response.getBody()) {} // Ensure that the connection is closed properly.

            return transferWasSuccessful;

        } catch (IOException | SOSURLException | ManifestNotFoundException e) {
            SOS_LOG.log(LEVEL.ERROR, "transferManifestRequest failed for manifest " + manifest.guid() + " and node " + node.guid().toMultiHash());
        }

        return false;
    }

    private URL getManifestURL(Node node, ManifestType type) throws SOSURLException {

        switch(type) {

            case ATOM: case ATOM_PROTECTED:
            case COMPOUND: case COMPOUND_PROTECTED:
            case VERSION:

                if (node.isDDS()) {
                    return SOSURL.DDS_POST_MANIFEST(node);
                }

            case ROLE:

                if (node.isRMS()) {
                    return SOSURL.USRO_POST_ROLE_MANIFEST(node);
                }

            case USER:

                if (node.isRMS()) {
                    return SOSURL.USRO_POST_USER_MANIFEST(node);
                }

            case CONTEXT:

                if (node.isCMS()) {
                    return SOSURL.CMS_POST_MANIFEST(node);
                }

            case METADATA: case METADATA_PROTECTED:

                if (node.isMMS()) {
                    return SOSURL.MMS_POST_MANIFEST(node);
                }

            case NODE:

                if (node.isNDS()) {
                    return SOSURL.NDS_POST_MANIFEST(node);
                }

            default:
                throw new SOSURLException("Unable to return manifest URL for node " + node.toString());
        }
    }

    private String manifestToSend(Manifest manifest) throws ManifestNotFoundException {

        ManifestType manifestType = manifest.getType();
        switch(manifestType) {
            case CONTEXT: // Transfer context with its predicate and policies.

                Context context = (Context) manifest;
                Predicate predicate = (Predicate) manifestsDataService.getManifest(context.predicate());
                Set<Policy> policies = new LinkedHashSet<>();
                for(IGUID policyRef:context.policies()) {
                    Policy policy = (Policy) manifestsDataService.getManifest(policyRef);
                    policies.add(policy);
                }

                try {
                    return ((Context) manifest).toFATString(predicate, policies);
                } catch (JsonProcessingException e) {
                    throw new ManifestNotFoundException("Unable to make FAT Context JSON");
                }

            default:
                return manifest.toString();
        }

    }
}
