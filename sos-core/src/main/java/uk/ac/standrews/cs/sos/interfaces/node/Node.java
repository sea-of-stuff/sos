package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.node.ROLE;

import java.net.InetSocketAddress;

/**
 * Node class - this defines an entry point in the LocalSOSNode and contains information
 * on how to talk to the node over the network
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
     * Set the roles of this node.
     * This method can be called only once.
     *
     * @param roles
     */
    void setRoles(byte roles);

    /**
     * Return the available roles for this node.
     * @return
     */
    ROLE[] getRoles();

    /**
     * Return the available roles for this node.
     * @return
     */
    byte getRolesInBytes();

}
