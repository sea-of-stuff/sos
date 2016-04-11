package uk.ac.standrews.cs.sos.network;

import uk.ac.standrews.cs.IGUID;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNode implements Node {

    @Override
    public IGUID getNodeGUID() {
        return null;
    }

    @Override
    public InetSocketAddress getHostAddress() {
        return null;
    }

    @Override
    public NodeType getNodeType() {
        return null;
    }

    @Override
    public Collection<IGUID> getKnownNodes() {
        return null;
    }
}
