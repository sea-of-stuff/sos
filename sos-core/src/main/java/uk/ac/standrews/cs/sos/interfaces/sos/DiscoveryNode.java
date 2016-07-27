package uk.ac.standrews.cs.sos.interfaces.sos;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface DiscoveryNode extends SeaOfStuff {

    /**
     * Get a known node to this Sea Of Stuff.
     * Client and IStorage will not support this call.
     *
     * @param guid
     * @return
     */
    Node getNode(IGUID guid);

    /**
     * Registers a node to the SOS
     * @param node
     * @return
     */
    void registerNode(Node node);
}
