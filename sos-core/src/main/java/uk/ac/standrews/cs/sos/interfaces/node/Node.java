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

    boolean isClient();

    boolean isStorage();

    boolean isDDS();

    boolean isNDS();

    boolean isMCS();

}
