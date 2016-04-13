package uk.ac.standrews.cs.sos.network;

import uk.ac.standrews.cs.IGUID;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Node {

    IGUID getNodeGUID();

    InetSocketAddress getHostAddress();

    /**
     * This returns an byte representing the type for this node.
     * @see NodeTypeMasks
     *
     * @return byte representing the type for this node
     */
    byte getNodeType();

}
