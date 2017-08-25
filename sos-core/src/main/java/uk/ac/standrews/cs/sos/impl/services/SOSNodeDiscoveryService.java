package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.impl.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.interfaces.node.Database;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.GetNode;
import uk.ac.standrews.cs.sos.protocol.tasks.RegisterNode;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The SOSNDS represents a basic NDS implementation.
 * It provides naive methods to register new nodes in the sos and get known nodes.
 *
 * TODO - pass scope as argument to methods
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNodeDiscoveryService implements NodeDiscoveryService {

    public static final int NO_LIMIT = 0;
    private LocalNodesDirectory localNodesDirectory;

    public SOSNodeDiscoveryService(Node localNode, Database database) throws NodesDirectoryException {
        localNodesDirectory = new LocalNodesDirectory(localNode, database);
    }

    @Override
    public Node getThisNode() {
        return localNodesDirectory.getLocalNode();
    }

    @Override
    public Node registerNode(Node node, boolean localOnly) throws NodeRegistrationException {
        if (node == null) {
            throw new NodeRegistrationException("Invalid node");
        }

        Node nodeToRegister = new SOSNode(node);

        try {
            localNodesDirectory.addNode(nodeToRegister);
            localNodesDirectory.persistNodesTable();
        } catch (NodesDirectoryException e) {
            throw new NodeRegistrationException("Unable to register node", e);
        }

        // Register the node to other NDS nodes
        if (!localOnly) {
            Set<Node> ndsNodes = getNodes(NodeType.NDS);
            ndsNodes.forEach(n -> {
                RegisterNode registerNode = new RegisterNode(node, n);
                TasksQueue.instance().performAsyncTask(registerNode);
            });
        }

        return nodeToRegister;
    }

    @Override
    public Node getNode(IGUID guid) throws NodeNotFoundException {

        if (guid == null || guid.isInvalid()) {
            throw new NodeNotFoundException("Cannot find node for invalid GUID");
        }

        Node localNode = localNodesDirectory.getLocalNode();
        if (localNode.getNodeGUID().equals(guid)) {
            return localNode;
        }

        Node nodeToContact = localNodesDirectory.getNode(guid);

        if (nodeToContact == null) {
            nodeToContact = findNodeViaNDS(guid);
        }

        if (nodeToContact == null) {
            throw new NodeNotFoundException("Unable to find node for GUID: " + guid.toString());
        } else {
            SOS_LOG.log(LEVEL.INFO, "Node with GUID " + guid + " was found: " + nodeToContact.toString());
        }

        return nodeToContact;
    }

    @Override
    public Set<Node> getNodes(NodeType type) {

        switch(type) {
            case STORAGE:
                return localNodesDirectory.getNodes(Node::isStorage, NO_LIMIT);
            case NDS:
                return localNodesDirectory.getNodes(Node::isNDS, NO_LIMIT);
            case CMS:
                return localNodesDirectory.getNodes(Node::isCMS, NO_LIMIT);
            case DDS:
                return localNodesDirectory.getNodes(Node::isDDS, NO_LIMIT);
            case RMS:
                return localNodesDirectory.getNodes(Node::isRMS, NO_LIMIT);
            case MMS:
                return localNodesDirectory.getNodes(Node::isMMS, NO_LIMIT);
        }

        return Collections.emptySet();
    }

    @Override
    public Set<Node> getNodes(NodesCollection nodesCollection, int limit) {

        if (nodesCollection.type().equals(NodesCollection.TYPE.ANY))
            return getNodes(limit);

        Set<Node> retval = new LinkedHashSet<>();
        for(IGUID guid : nodesCollection.nodesRefs()) {

            try {
                retval.add(getNode(guid));
            } catch (NodeNotFoundException e) {
                continue;
            }

            if (retval.size() > limit) break;
        }


        return retval;
    }

    @Override
    public NodesCollection filterNodesCollection(NodesCollection nodesCollection, NodeType type, int limit) {

        if (nodesCollection.type().equals(NodesCollection.TYPE.ANY) && limit == NO_LIMIT) return nodesCollection;


        Set<IGUID> filteredNodes = new LinkedHashSet<>();
        for(IGUID nodeRef : nodesCollection.nodesRefs()) {

            // COMPARE node's type with type passed in this method
            // add node to filteredNodes
            try {
                Node node = getNode(nodeRef);
                switch(type) {
                    case STORAGE:
                        if (node.isStorage()) filteredNodes.add(nodeRef); break;
                    case NDS:
                        if (node.isNDS()) filteredNodes.add(nodeRef); break;
                    case CMS:
                        if (node.isCMS()) filteredNodes.add(nodeRef); break;
                    case DDS:
                        if (node.isDDS()) filteredNodes.add(nodeRef); break;
                    case RMS:
                        if (node.isRMS()) filteredNodes.add(nodeRef); break;
                    case MMS:
                        if (node.isMMS()) filteredNodes.add(nodeRef); break;
                }
            } catch (NodeNotFoundException e) {
                SOS_LOG.log(LEVEL.WARN, "Unable to get node with ref: " + nodeRef);
            }

            if (filteredNodes.size() == limit && limit != NO_LIMIT)
                break;
        }

        return new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, filteredNodes);
    }

    @Override
    public Set<Node> getNodes() {
        return getNodes(NO_LIMIT);
    }

    @Override
    public Set<Node> getNodes(int limit) {
        return localNodesDirectory.getNodes(p -> true, limit);
    }

    /**
     * Find a matching node for the given GUID through other known NDS nodes
     */
    private Node findNodeViaNDS(IGUID nodeGUID) throws NodeNotFoundException {

        Set<Node> ndsNodes = getNodes(NodeType.NDS);
        GetNode getNode = new GetNode(nodeGUID, ndsNodes.iterator());
        TasksQueue.instance().performSyncTask(getNode);

        Node retval = getNode.getFoundNode();
        if (retval == null) {
            throw new NodeNotFoundException("Unable to find node with GUID " + nodeGUID);
        }

        return retval;
    }
}
