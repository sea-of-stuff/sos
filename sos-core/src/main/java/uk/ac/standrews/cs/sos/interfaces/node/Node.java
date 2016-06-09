package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.IGUID;

import java.net.InetSocketAddress;

/**
 * Node class - this defines an entry point in the SOS and contains information
 * on how to talk to the node over the network
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Node {

    IGUID getNodeGUID();

    InetSocketAddress getHostAddress();

}
