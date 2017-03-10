package uk.ac.standrews.cs.sos.json.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.net.InetSocketAddress;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeModel implements Node {

    private String guid;
    private String hostname;
    private int port;
    private RolesModel roles;

    public NodeModel() {}

    @JsonIgnore
    public IGUID getNodeGUID() {

        try {
            return GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }

        return null;
    }

    @JsonIgnore
    public InetSocketAddress getHostAddress() {
        return new InetSocketAddress(hostname, port);
    }

    @Override
    public boolean isAgent() {
        return roles.isAgent();
    }

    @Override
    public boolean isStorage() {
        return roles.isStorage();
    }

    @Override
    public boolean isDDS() {
        return roles.isDDS();
    }

    @Override
    public boolean isNDS() {
        return roles.isNDS();
    }

    @Override
    public boolean isMMS() {
        return roles.isMMS();
    }

    @Override
    public boolean isCMS() {
        return roles.isCMS();
    }

    @Override
    public boolean isRMS() {
        return roles.isRMS();
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public RolesModel getRoles() {
        return roles;
    }

    public void setRoles(RolesModel roles) {
        this.roles = roles;
    }
}
