package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.IGUID;

import java.net.InetSocketAddress;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Node {

    IGUID getNodeGUID();

    InetSocketAddress getHostAddress();

}
