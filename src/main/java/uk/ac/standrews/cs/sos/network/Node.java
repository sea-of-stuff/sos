package uk.ac.standrews.cs.sos.network;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.network.roles.NodeRolesMasks;

import java.net.InetSocketAddress;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Node {

    IGUID getNodeGUID();

    InetSocketAddress getHostAddress();

    /**
     * This returns an byte representing the type for this node.
     * @see NodeRolesMasks
     *
     * @return byte representing the type for this node
     */
    byte getNodeRole();

}
