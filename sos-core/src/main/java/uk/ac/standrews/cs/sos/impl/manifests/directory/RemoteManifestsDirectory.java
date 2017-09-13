package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.FetchManifest;
import uk.ac.standrews.cs.sos.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.standrews.cs.sos.constants.Internals.REPLICATION_FACTOR_MULTIPLIER;
import static uk.ac.standrews.cs.sos.impl.services.SOSNodeDiscoveryService.NO_LIMIT;

/**
 * The remote manifest directory allows the node to replicate manifests to other nodes in the SOS
 * as well as finding manifests in the rest of the SOS
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteManifestsDirectory extends AbstractManifestsDirectory implements ManifestsDirectory {

    private DDSIndex ddsIndex;
    private NodeDiscoveryService nodeDiscoveryService;
    private DataDiscoveryService dataDiscoveryService;

    public RemoteManifestsDirectory(DDSIndex ddsIndex, NodeDiscoveryService nodeDiscoveryService, DataDiscoveryService dataDiscoveryService) {
        this.ddsIndex = ddsIndex;
        this.nodeDiscoveryService = nodeDiscoveryService;
        this.dataDiscoveryService = dataDiscoveryService;
    }

    /**
     * Async operation
     *
     * @param manifest
     * @throws ManifestPersistException
     */
    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {

        try {
            NodesCollection replicationNode = nodeDiscoveryService.filterNodesCollection(new NodesCollectionImpl(NodesCollection.TYPE.ANY), NodeType.DDS, 1);
            ManifestReplication replicationTask = new ManifestReplication(manifest, replicationNode, 1, nodeDiscoveryService, dataDiscoveryService);
            TasksQueue.instance().performAsyncTask(replicationTask);
        } catch (SOSProtocolException | NodesCollectionException e) {
            throw new ManifestPersistException("Unable to persist node to remote nodes");
        }

    }

    public void addManifest(Manifest manifest, NodesCollection nodesCollection, int replicationFactor) throws ManifestPersistException {

        NodesCollection replicationNodes = nodeDiscoveryService.filterNodesCollection(nodesCollection, NodeType.DDS, replicationFactor * REPLICATION_FACTOR_MULTIPLIER);
        try {
            // The replication task takes care of replicating the manifest and updating the ManifestDDSMapping if the replication is successful
            ManifestReplication replicationTask = new ManifestReplication(manifest, replicationNodes, replicationFactor, nodeDiscoveryService, dataDiscoveryService);
            TasksQueue.instance().performAsyncTask(replicationTask);
        } catch (SOSProtocolException e) {
            throw new ManifestPersistException("Unable to persist node to remote nodes");
        }
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

        try {
            return findManifest(guid, new NodesCollectionImpl(NodesCollection.TYPE.ANY));
        } catch (NodesCollectionException e) {
            return null;
        }

    }

    public Manifest findManifest(IGUID guid, NodesCollection nodesCollection) throws ManifestNotFoundException {

        Set<IGUID> ddsGUIDsToCheck;
        if (nodesCollection.type().equals(NodesCollection.TYPE.SPECIFIED)) {
            NodesCollection ddsNodesOnly = nodeDiscoveryService.filterNodesCollection(nodesCollection, NodeType.DDS, NO_LIMIT);
            ddsGUIDsToCheck = ddsNodesOnly.nodesRefs();
        } else {
            // Get DDS nodes where we know the entity could be
            ddsGUIDsToCheck = ddsIndex.getDDSRefs(guid);
        }

        if (ddsGUIDsToCheck == null) {

            // Simply check any node
            ddsGUIDsToCheck = nodeDiscoveryService.getNodes(NodeType.DDS).stream() // FIXME - this call can be improved
                                    .map(Node::getNodeGUID)
                                    .collect(Collectors.toSet());
        }

        if (ddsGUIDsToCheck == null) {
            throw new ManifestNotFoundException("Unable to find manifest because there are no known DDS nodes");
        }

        for(IGUID ddsGUID : ddsGUIDsToCheck) {

            try {
                Node node = nodeDiscoveryService.getNode(ddsGUID);

                FetchManifest fetchManifest = new FetchManifest(node, guid); // FIXME - use different end-points for context, metadata, etc
                TasksQueue.instance().performSyncTask(fetchManifest);

                Manifest retval = fetchManifest.getManifest();
                return retval;

            } catch (NodeNotFoundException | IOException e) {
                SOS_LOG.log(LEVEL.WARN, "A problem occurred while attempting to fetch a manifest with GUID " + guid .toMultiHash()+ " from Node with GUID " + ddsGUID.toMultiHash());
            }

        }

        throw new ManifestNotFoundException("Unable to find manifest in other known DDS nodes");
    }

    @Override
    public void flush() {}

}
