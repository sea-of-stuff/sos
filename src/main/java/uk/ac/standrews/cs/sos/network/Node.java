package uk.ac.standrews.cs.sos.network;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.network.roles.Role;
import uk.ac.standrews.cs.sos.network.roles.RoleMasks;

import java.net.InetSocketAddress;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Node {

    IGUID getNodeGUID();

    InetSocketAddress getHostAddress();

    /**
     * This returns an byte representing the type for this node.
     * @see RoleMasks
     *
     * @return byte representing the type for this node
     */
    byte getNodeRole();

    Node setNodeRole(Role role);

}
