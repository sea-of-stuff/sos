package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Node;

import java.util.Set;

/**
 * NDS - Node Discovery Service
 *
 * TODO - pass scope to methods
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface NDS extends SeaOfStuff {

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
     * @param localOnly if false, the node will be registered to other known NDS nodes
     * @return registered node
     */
    Node registerNode(Node node, boolean localOnly) throws NodeRegistrationException;

    /**
     * Get a known node to this Sea Of Stuff
     *
     * @param guid
     * @return the noda with the given guid
     */
    Node getNode(IGUID guid) throws NodeNotFoundException;

    /**
     * Get a set of nodes matching the specified type
     *
     * @param type of request nodes
     * @return set of nodes
     */
    Set<Node> getNodes(NodeType type);

    /**
     * Get all known nodes
     *
     * @return set of nodes
     */
    Set<Node> getAllNodes();

}
