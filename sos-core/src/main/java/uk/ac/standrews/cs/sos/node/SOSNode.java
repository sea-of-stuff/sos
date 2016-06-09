package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNode implements Node {

    private static int SOS_NODE_DEFAULT_PORT = 8080;

    private IGUID nodeGUID;
    private InetSocketAddress hostAddress;

    public SOSNode(IGUID guid) {
        this.nodeGUID = guid;
        this.hostAddress = new InetSocketAddress(SOS_NODE_DEFAULT_PORT);
    }

    public SOSNode(IGUID guid, InetSocketAddress hostAddress) {
        this(guid);
        this.hostAddress = hostAddress;
    }

    @Override
    public IGUID getNodeGUID() {
        return nodeGUID;
    }

    @Override
    public InetSocketAddress getHostAddress() {
        return hostAddress;
    }

    @Override
    public String toString() {
        return nodeGUID.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SOSNode sosNode = (SOSNode) o;
        return Objects.equals(nodeGUID, sosNode.nodeGUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeGUID);
    }

}
