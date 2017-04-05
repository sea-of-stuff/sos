package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.node.NodeException;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNode implements Node {

    private IGUID nodeGUID;
    protected InetSocketAddress hostAddress;

    /******************
     * DB fields *
     ******************/
    private String DB_nodeid;
    private String DB_hostname;
    private int DB_port;
    protected boolean DB_is_agent;
    protected boolean DB_is_storage;
    protected boolean DB_is_dds;
    protected boolean DB_is_nds;
    protected boolean DB_is_mms;
    protected boolean DB_is_cms;
    protected boolean DB_is_rms;

    // no-args constructor needed for ORMLite
    protected SOSNode() {}

    public SOSNode(IGUID guid, String hostname, int port,
                   boolean isAgent, boolean isStorage, boolean isDDS,
                   boolean isNDS, boolean isMMS, boolean isCMS, boolean isRMS) {
        this.nodeGUID = guid;
        this.hostAddress = new InetSocketAddress(hostname, port);

        this.DB_nodeid = guid.toString();
        this.DB_hostname = hostname;
        this.DB_port = port;
        this.DB_is_agent = isAgent;
        this.DB_is_storage = isStorage;
        this.DB_is_dds = isDDS;
        this.DB_is_nds = isNDS;
        this.DB_is_mms = isMMS;
        this.DB_is_cms = isCMS;
        this.DB_is_rms = isRMS;
    }

    public SOSNode(SOSConfiguration configuration) throws NodeException {

        try {
            this.nodeGUID = configuration.getNodeGUID();

            InetAddress hostname = InetAddress.getLocalHost();
            int port = configuration.getNodePort();
            this.hostAddress = new InetSocketAddress(hostname, port);

            this.DB_nodeid = nodeGUID.toString();
            this.DB_hostname = hostname.getHostAddress();
            this.DB_port = port;

            this.DB_is_agent = configuration.nodeIsAgent();
            this.DB_is_storage = configuration.nodeIsStorage();
            this.DB_is_dds = configuration.nodeIsDDS();
            this.DB_is_nds = configuration.nodeIsNDS();
            this.DB_is_mms = configuration.nodeIsMMS();
            this.DB_is_cms = configuration.nodeIsCMS();
            this.DB_is_rms = configuration.nodeIsRMS();
        } catch (GUIDGenerationException | IOException e) {
            throw new NodeException(e);
        }
    }

    // Cloning constructor
    public SOSNode(Node node) {
        this(node.getNodeGUID(), node.getHostAddress().getHostName(), node.getHostAddress().getPort(),
                node.isAgent(), node.isStorage(), node.isDDS(),
                node.isNDS(), node.isMMS(), node.isCMS(), node.isRMS());
    }

    @Override
    public IGUID getNodeGUID() {

        if (nodeGUID == null) {
            try {
                nodeGUID = GUIDFactory.recreateGUID(DB_nodeid);
            } catch (GUIDGenerationException e) {
                e.printStackTrace();
                // TODO - throw appropriate exception
            }
        }

        return nodeGUID;
    }

    @Override
    public InetSocketAddress getHostAddress() {

        if (hostAddress == null) {
            hostAddress = new InetSocketAddress(DB_hostname, DB_port);
        }

        return hostAddress;
    }

    @Override
    public boolean isAgent() {
        return DB_is_agent;
    }

    @Override
    public boolean isStorage() {
        return DB_is_storage;
    }

    @Override
    public boolean isDDS() {
        return DB_is_dds;
    }

    @Override
    public boolean isNDS() {
        return DB_is_nds;
    }

    @Override
    public boolean isMMS() {
        return DB_is_mms;
    }

    @Override
    public boolean isCMS() {
        return DB_is_cms;
    }

    @Override
    public boolean isRMS() {
        return DB_is_rms;
    }

    // TODO - use json-deser via jackson
    @Override
    public String toString() {
        String json = "{ ";
        {
            json += "\"guid\" : \"" + getNodeGUID() + "\", ";
            json += "\"hostname\" : \"" + getHostAddress().getHostName() + "\", ";
            json += "\"port\" : " + getHostAddress().getPort() + ", ";

            json += "\"roles\" : ";
            json += "{";
            {
                json += "\"agent\" : " + isAgent() + ", ";
                json += "\"storage\" : " + isStorage() + ", ";
                json += "\"dds\" : " + isDDS() + ", ";
                json += "\"nds\" : " + isNDS() + ", ";
                json += "\"mms\" : " + isMMS() + ", ";
                json += "\"cms\" : " + isCMS() + ", ";
                json += "\"rms\" : " + isRMS();
            }
            json += "}";
        }
        json += " }";

        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SOSNode sosNode = (SOSNode) o;
        return Objects.equals(getNodeGUID(), sosNode.getNodeGUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeGUID());
    }

}
