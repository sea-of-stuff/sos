package uk.ac.standrews.cs.sos.network;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.network.roles.RoleMasks;
import uk.ac.standrews.cs.sos.network.roles.Role;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNode implements Node {

    private IGUID nodeGUID;
    private InetSocketAddress hostAddress;

    private HashSet<Role> roles;

    public SOSNode(IGUID guid) {
        this();
        this.nodeGUID = guid;
        // TODO - this node?
    }

    public SOSNode() {
        roles = new LinkedHashSet<>();
    }

    public SOSNode(IGUID guid, InetSocketAddress hostAddress) {
        // Contact node and get port? or specify port here
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
    public byte getNodeRole() {
        byte nodeRole = RoleMasks.VOID_MASK;
        for(Role role:roles) {
            nodeRole |= role.getRoleMask();
        }
        return nodeRole;
    }

    @Override
    public Node setNodeRole(Role role) {
        roles.add(role);
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
