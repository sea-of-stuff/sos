package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.model.Node;

import java.util.Iterator;
import java.util.Set;

/**
 * NDS - Node Discovery Service
 *
 * Nodes that have this Role allow nodes in the SOS to discovery each other
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface NDS extends SeaOfStuff {

    /**
     * Get a node object for the local node
     *
     * @return
     */
    Node getThisNode();

    /**
     * Registers a node to the SOS
     *
     * @param node
     * @param localOnly if false, the node will be registered to other known NDS nodes
     * @return
     */
    Node registerNode(Node node, boolean localOnly) throws NodeRegistrationException;

    /**
     * Get a known node to this Sea Of Stuff
     *
     * @param guid
     * @return the noda with the given guid
     */
    Node getNode(IGUID guid) throws NodeNotFoundException;

    Set<Node> getNDSNodes();

    Set<Node> getDDSNodes();
    Iterator<Node> getDDSNodesIterator();

    Set<Node> getMMSNodes();

    Set<Node> getStorageNodes();
    Iterator<Node> getStorageNodesIterator();

    Set<Node> getCMSNodes();

    Set<Node> getRMSNodes();

    Set<Node> getAllNodes();

}
