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
 * TODO - pass scope to methods
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

    /**
     * Get the known NDS nodes
     *
     * @return
     */
    Set<Node> getNDSNodes();

    /**
     * Get the known DDS nodes
     *
     * @return
     */
    Set<Node> getDDSNodes();
    Iterator<Node> getDDSNodesIterator();

    /**
     * Get the known MMS nodes
     *
     * @return
     */
    Set<Node> getMMSNodes();

    /**
     * Get the known storage nodes
     *
     * @return
     */
    Set<Node> getStorageNodes();
    Iterator<Node> getStorageNodesIterator();

    /**
     * Get the known CMS nodes
     *
     * @return
     */
    Set<Node> getCMSNodes();

    /**
     * Get the known RMS nodes
     *
     * @return
     */
    Set<Node> getRMSNodes();

    /**
     * Get all known nodes
     *
     * @return
     */
    Set<Node> getAllNodes();

}
