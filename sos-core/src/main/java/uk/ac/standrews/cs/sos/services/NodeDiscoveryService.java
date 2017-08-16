package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.util.Set;

/**
 * NDS - Node Discovery Service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface NodeDiscoveryService {

    /**
     * Get a node object for the local node
     *
     * @return this node
     */
    Node getThisNode();

    /**
     * Registers a node to the SOS
     *
     * @param node to register
     * @param localOnly if true, the node will be registered to this node only. Otherwise, if false, the node will be registered to other known NDS nodes
     * @return registered node
     */
    Node registerNode(Node node, boolean localOnly) throws NodeRegistrationException;

    /**
     * Get a known node to this Sea Of Stuff
     *
     * @param guid of the node to find
     * @return the noda with the given guid
     */
    Node getNode(IGUID guid) throws NodeNotFoundException;

    /**
     * Get a set of nodes matching the specified type
     *
     * @param type of request nodes
     * @return set of nodes
     */
    Set<Node> getNodes(NodeType type); // TODO - use method below ?!?

    /**
     * Get a set of nodes from a nodes collection.
     * The limit parameters will constrain the size of the set.
     *
     * @param nodesCollection
     * @param limit
     * @return
     */
    Set<Node> getNodes(NodesCollection nodesCollection, int limit);

    /**
     * Returns a set of node refs matching the NodesCollection and NodeType constraints.
     * This method returns a maximum number of nodes as specified by the limit parameter.
     *
     * @param nodesCollection
     * @param type
     * @param limit
     * @return the filtered nodes collection
     */
    NodesCollection filterNodesCollection(NodesCollection nodesCollection, NodeType type, int limit);

    /**
     * Get all known nodes
     *
     * @return set of nodes
     */
    Set<Node> getNodes();

    /**
     * Returns a set of known nodes.
     * The limit constrains the maximum number of nodes returned.
     *
     * @param limit
     * @return
     */
    Set<Node> getNodes(int limit);

}
