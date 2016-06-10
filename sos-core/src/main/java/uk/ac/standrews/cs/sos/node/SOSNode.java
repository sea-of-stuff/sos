package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNode implements Node {

    private static int SOS_NODE_DEFAULT_PORT = 8080;

    private byte roles;
    private HashSet<ROLE> rolesSet;
    private IGUID nodeGUID;
    private InetSocketAddress hostAddress;

    public SOSNode(IGUID guid) {
        this.rolesSet = new LinkedHashSet<>();
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
    public void setRoles(byte roles) {

        if (!rolesSet.isEmpty()) {
            return;
        }

        this.roles = roles;
        if ((roles & ROLE.COORDINATOR.mask) != 0) {
            rolesSet.add(ROLE.COORDINATOR);
        }

        if ((roles & ROLE.CLIENT.mask) != 0) {
            rolesSet.add(ROLE.CLIENT);
        }

        if ((roles & ROLE.STORAGE.mask) != 0) {
            rolesSet.add(ROLE.STORAGE);
        }
    }

    @Override
    public ROLE[] getRoles() {
        return (ROLE[]) rolesSet.toArray();
    }

    @Override
    public byte getRolesInBytes() {
        return roles;
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
