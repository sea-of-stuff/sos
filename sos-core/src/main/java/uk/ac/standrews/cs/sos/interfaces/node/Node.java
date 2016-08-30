package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.IGUID;

import java.net.InetSocketAddress;

/**
 * Node interface
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Node {

    /**
     * This is the unique GUID for this node.
     * The GUID is generated using a pseudo-random hash function.
     *
     * @return
     */
    IGUID getNodeGUID();

    /**
     * This is the address of the node.
     * This information should be used to contact the node.
     *
     * @return
     */
    InetSocketAddress getHostAddress();

    /**
     * Returns true if this is a client node
     * @return
     */
    boolean isClient();

    /**
     * Returns true if this is a storage node
     * @return
     */
    boolean isStorage();

    /**
     * Returns true if this is a DDS node
     * @return
     */
    boolean isDDS();

    /**
     * Returns true if this is a NDS node
     * @return
     */
    boolean isNDS();

    /**
     * Returns true if this is a MCS node
     * @return
     */
    boolean isMCS();

}
