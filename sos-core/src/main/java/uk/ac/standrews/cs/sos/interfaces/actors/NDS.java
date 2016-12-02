package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.util.Set;

/**
 * NDS - Node Discovery Service
 *
 * Nodes that have this Role allow nodes in the SOS to discovery each other
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface NDS extends SeaOfStuff {

    Node getThisNode();

    /**
     * Registers a node to the SOS
     * @param node
     * @return
     */
    Node registerNode(Node node) throws NodeRegistrationException;

    /**
     * Get a known node to this Sea Of Stuff.
     * Client and IStorage will not support this call.
     *
     * @param guid
     * @return
     */
    Node getNode(IGUID guid) throws NodeNotFoundException;

    /**
     * Return all matching NDS nodes
     *
     * @return an empty collection if there are not matching nodes
     */
    Set<Node> getNDSNodes();

    Set<Node> getDDSNodes();
    Set<Node> getDDSNodes(int limit);

    Set<Node> getMCSNodes();

    Set<Node> getStorageNodes();

    /**
     * Return a set of storage nodes. This method will return maximum the number of nodes specified
     * by the limit parameter.
     * This method may return less nodes than specified by the limit if there are not enough nodes.
     *
     * @param limit
     * @return
     */
    Set<Node> getStorageNodes(int limit);

}
