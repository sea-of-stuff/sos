package uk.ac.standrews.cs.sos.interfaces.sos;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.util.Collection;

/**
 * NDS - Node Discovery Service
 *
 * Nodes that have this Role allow nodes in the SOS to discovery each other
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface NDS extends SeaOfStuff {

    /**
     * Registers a node to the SOS
     * @param node
     * @return
     */
    Node registerNode(Node node);

    /**
     * Get a known node to this Sea Of Stuff.
     * Client and IStorage will not support this call.
     *
     * @param guid
     * @return
     */
    Node getNode(IGUID guid);

    /**
     * Return all matching NDS nodes
     *
     * @return an empty collection if there are not matching nodes
     */
    Collection<Node> getNDSNodes();

    Collection<Node> getDDSNodes();

    Collection<Node> getMCSNodes();

    Collection<Node> getStorageNodes();
}
