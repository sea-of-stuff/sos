package uk.ac.standrews.cs.sos.network;

import uk.ac.standrews.cs.IGUID;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNode implements Node {

    private IGUID nodeGUID;
    private InetSocketAddress hostAddress;
    private byte nodeType;

    public SOSNode(IGUID guid) {
        this.nodeGUID = guid;
        // TODO - this node?
    }

    public SOSNode() {
        // TODO - load node
    }

    public SOSNode(IGUID guid, InetSocketAddress hostAddress) {
        // Contact node and get port? or specify port here
        this.nodeGUID = guid;
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
    public byte getNodeType() {
        return nodeType;
    }

    public Node setNodeType(byte nodeType) {
        this.nodeType = nodeType;
        return this;
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
