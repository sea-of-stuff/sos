package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.actors.protocol.tasks.GetNode;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.tasks.TasksQueue;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Iterator;
import java.util.Set;


/**
 *
 * ideas:
 * round robin
 * by type
 * broadcasting/multicasting
 * get list of nodes from a well known url end-point
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeDiscovery {

    private LocalNodesDirectory localNodesDirectory;

    public NodeDiscovery(LocalNodesDirectory localNodesDirectory) {
        this.localNodesDirectory = localNodesDirectory;
    }

    public Node getLocalNode() {
        return localNodesDirectory.getLocalNode();
    }

    /**
     * Attempt to find a node matching the given GUID first locally, then by contacting known NDS nodes.
     *
     * @param nodeGUID
     * @return
     * @throws NodeNotFoundException
     */
    public Node findNode(IGUID nodeGUID) throws NodeNotFoundException {

        if (nodeGUID == null || nodeGUID.isInvalid()) {
            throw new NodeNotFoundException("Cannot find node for invalid GUID");
        }

        Node localNode = localNodesDirectory.getLocalNode();
        if (localNode.getNodeGUID().equals(nodeGUID)) {
            return localNode;
        }

        Node nodeToContact = localNodesDirectory.getNode(nodeGUID);

        if (nodeToContact == null) {
            nodeToContact = findNodeViaNDS(nodeGUID);
        }

        if (nodeToContact == null) {
            throw new NodeNotFoundException("Unable to find node for GUID: " + nodeGUID.toString());
        } else {
            SOS_LOG.log(LEVEL.INFO, "Node with GUID " + nodeGUID + " was found: " + nodeToContact.toString());
        }

        return nodeToContact;
    }

    /**
     * Get all NDS Nodes
     *
     * @return NDS Nodes
     */
    public Set<Node> getNDSNodes() {
        return localNodesDirectory.getNDSNodes(LocalNodesDirectory.NO_LIMIT);
    }

    /**
     * Get all DDS Nodes
     *
     * @return DDS nodes
     */
    public Set<Node> getDDSNodes() {
        return localNodesDirectory.getDDSNodes(LocalNodesDirectory.NO_LIMIT);
    }

    public Iterator<Node> getDDSNodesIterator() {
        return localNodesDirectory.getDDSNodesIterator();
    }

    /**
     * Get all MCS Nodes
     *
     * @return MCS nodes
     */
    public Set<Node> getMCSNodes() {
        return localNodesDirectory.getMCSNodes(LocalNodesDirectory.NO_LIMIT);
    }

    /**
     * Get all Storage Nodes
     *
     * @return Storage nodes
     */
    public Set<Node> getStorageNodes() {
        return localNodesDirectory.getStorageNodes(LocalNodesDirectory.NO_LIMIT);
    }

    public Iterator<Node> getStorageNodesIterator() {
        return localNodesDirectory.getStorageNodesIterator();
    }

    private Node findNodeViaNDS(IGUID nodeGUID) throws NodeNotFoundException {

        Set<Node> ndsNodes = localNodesDirectory.getNDSNodes(LocalNodesDirectory.NO_LIMIT);
        GetNode getNode = new GetNode(nodeGUID, ndsNodes.iterator());
        TasksQueue.instance().performSyncTask(getNode);

        Node retval = getNode.getFoundNode();
        if (retval == null) {
            throw new NodeNotFoundException("Unable to find node with GUID " + nodeGUID);
        }

        return retval;
    }

}
