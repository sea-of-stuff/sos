package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.FetchManifest;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.FetchVersions;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.NodesCollectionType;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.LinkedHashSet;
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

    /**
     * Max number of trials to external nodes before throwing a not found exception
     */
    private static int NUMBER_OF_REMOTE_TRIALS = 3;

    private ManifestsLocationsIndex manifestsLocationsIndex;
    private NodeDiscoveryService nodeDiscoveryService;
    private ManifestsDataService manifestsDataService;

    public RemoteManifestsDirectory(ManifestsLocationsIndex manifestsLocationsIndex, NodeDiscoveryService nodeDiscoveryService, ManifestsDataService manifestsDataService) {
        this.manifestsLocationsIndex = manifestsLocationsIndex;
        this.nodeDiscoveryService = nodeDiscoveryService;
        this.manifestsDataService = manifestsDataService;
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
            NodeType nodeType = getNodeType(manifest);
            if (nodeType == null) throw new SOSProtocolException("Unable to tell what node type to talk to");

            NodesCollection replicationNode = nodeDiscoveryService.filterNodesCollection(new NodesCollectionImpl(NodesCollectionType.ANY), nodeType, 1);
            ManifestReplication replicationTask = new ManifestReplication(manifest, replicationNode, 1, nodeDiscoveryService, manifestsDataService);
            TasksQueue.instance().performAsyncTask(replicationTask);
        } catch (SOSProtocolException | NodesCollectionException e) {
            throw new ManifestPersistException("Unable to persist node to remote nodes");
        }

    }

    /**
     * Async operation
     *
     * @param manifest
     * @param nodesCollection
     * @param replicationFactor
     * @throws ManifestPersistException
     */
    public void addManifest(Manifest manifest, NodesCollection nodesCollection, int replicationFactor) throws ManifestPersistException {

        try {
            NodeType nodeType = getNodeType(manifest);
            if (nodeType == null) throw new SOSProtocolException("Unable to tell what node type to talk to");

            NodesCollection replicationNodes = nodeDiscoveryService.filterNodesCollection(nodesCollection, nodeType, replicationFactor * REPLICATION_FACTOR_MULTIPLIER);
            // The replication task takes care of replicating the manifest and updating the ManifestDDSMapping if the replication is successful
            ManifestReplication replicationTask = new ManifestReplication(manifest, replicationNodes, replicationFactor, nodeDiscoveryService, manifestsDataService);
            TasksQueue.instance().performAsyncTask(replicationTask);
        } catch (SOSProtocolException e) {
            throw new ManifestPersistException("Unable to persist node to remote nodes");
        }
    }

    private NodeType getNodeType(Manifest manifest) {

        switch(manifest.getType()) {

            case ATOM: case ATOM_PROTECTED:
            case COMPOUND: case COMPOUND_PROTECTED:
            case VERSION:
                return NodeType.DDS;

            case ROLE: case USER:
                return NodeType.RMS;

            case METADATA: case METADATA_PROTECTED:
                return NodeType.MMS;

            case NODE:
                return NodeType.NDS;

            case CONTEXT: case POLICY: case PREDICATE:
                return NodeType.CMS;

            default:
                return null;
        }
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

        try {
            return findManifest(new NodesCollectionImpl(NodesCollectionType.ANY), NodeType.DDS, guid);
        } catch (NodesCollectionException e) {
            throw new ManifestNotFoundException();
        }

    }

    public Manifest findManifest(NodesCollection nodesCollection, NodeType nodeTypeFilter, IGUID guid) throws ManifestNotFoundException {

        Set<IGUID> nodesToCheck;
        try {
            nodesToCheck = getNodesToCheck(nodesCollection, nodeTypeFilter, guid);
        } catch (NodeNotFoundException e) {
            throw new ManifestNotFoundException("Unable to find manifest because there are no known DDS nodes");
        }

        int trial = 0;
        for(IGUID nodeToCheck : nodesToCheck) {

            if (trial >= NUMBER_OF_REMOTE_TRIALS) {
                break;
            }
            trial++;

            try {
                Node node = nodeDiscoveryService.getNode(nodeToCheck);

                FetchManifest fetchManifest = new FetchManifest(node, guid);
                TasksQueue.instance().performSyncTask(fetchManifest);

                Manifest manifest = fetchManifest.getManifest();
                if (manifest == null) {
                    continue;
                }

                // Update the manifest-node mapping
                manifestsDataService.addManifestNodeMapping(manifest.guid(), nodeToCheck);

                return manifest;

            } catch (NodeNotFoundException | IOException e) {
                SOS_LOG.log(LEVEL.WARN, "A problem occurred while attempting to fetch a manifest with GUID " + guid .toMultiHash()+ " from Node with GUID " + nodeToCheck.toMultiHash());
            }

        }

        throw new ManifestNotFoundException("Unable to find manifest in other known DDS nodes");
    }

    public Set<IGUID> getVersions(IGUID invariant) {

        try {
            return getVersions(new NodesCollectionImpl(NodesCollectionType.ANY), invariant);
        } catch (NodesCollectionException e) {
            return new LinkedHashSet<>();
        }
    }

    public Set<IGUID> getVersions(NodesCollection nodesCollection, IGUID invariant) {


        Set<IGUID> nodesToCheck;
        try {
            nodesToCheck = getNodesToCheck(nodesCollection, NodeType.DDS, invariant);
        } catch (NodeNotFoundException e) {
            return new LinkedHashSet<>();
        }

        Set<IGUID> versionRefs = new LinkedHashSet<>();
        for(IGUID nodeToCheck : nodesToCheck) {

            try {
                Node node = nodeDiscoveryService.getNode(nodeToCheck);

                FetchVersions fetchVersions = new FetchVersions(node, invariant);
                TasksQueue.instance().performSyncTask(fetchVersions);

                versionRefs.addAll(fetchVersions.getVersions());

            } catch (NodeNotFoundException | IOException e) {
                SOS_LOG.log(LEVEL.WARN, "A problem occurred while attempting to fetch versions for invariant " + invariant.toMultiHash()+ " from Node with GUID " + nodeToCheck.toMultiHash());
            }

        }

        return versionRefs;
    }

    @Override
    public void flush() {}

    private Set<IGUID> getNodesToCheck(NodesCollection nodesCollection, NodeType nodeType, IGUID guid) throws NodeNotFoundException {

        Set<IGUID> nodesToCheck;
        if (nodesCollection.type().equals(NodesCollectionType.SPECIFIED)) {

            NodesCollection nodes = nodeDiscoveryService.filterNodesCollection(nodesCollection, nodeType, NO_LIMIT);
            nodesToCheck = nodes.nodesRefs();

        } else if (nodesCollection.type().equals(NodesCollectionType.ANY)){

            // Get nodes where we know the entity could be
            nodesToCheck = nodeDiscoveryService.getNodes(nodeType).stream()
                    .map(Node::guid)
                    .collect(Collectors.toSet());

        } else {

            nodesToCheck = manifestsLocationsIndex.getNodeRefs(guid);

        }

        // REMOVEME
//
//        if (nodesToCheck == null) {
//
//            // Simply get any node for the type specified
//            nodesToCheck = nodeDiscoveryService.getNodes(nodeType).stream()
//                    .map(Node::guid)
//                    .collect(Collectors.toSet());
//
//        }

        if (nodesToCheck == null) {
            throw new NodeNotFoundException("Unable to find manifest because there are no known DDS nodes");
        }

        return nodesToCheck;

    }

}
