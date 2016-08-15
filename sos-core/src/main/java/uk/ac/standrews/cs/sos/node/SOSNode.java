package uk.ac.standrews.cs.sos.node;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.node.NodeException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@DatabaseTable(tableName = "nodes")
public class SOSNode implements Node {

    private static int SOS_NODE_DEFAULT_PORT = 8080;

    private IGUID nodeGUID;
    private InetSocketAddress hostAddress;

    /******************
     * ORMLite fields *
     ******************/
    @DatabaseField(id = true)
    private String DB_nodeid;
    @DatabaseField(canBeNull = false)
    private String DB_hostname;
    @DatabaseField(canBeNull = false)
    private int DB_port;
    @DatabaseField(canBeNull = false)
    protected boolean DB_is_client;
    @DatabaseField(canBeNull = false)
    protected boolean DB_is_storage;
    @DatabaseField(canBeNull = false)
    protected boolean DB_is_dds;
    @DatabaseField(canBeNull = false)
    protected boolean DB_is_nds;
    @DatabaseField(canBeNull = false)
    protected boolean DB_is_mcs;

    // no-args constructor needed for ORMLite
    protected SOSNode() {}

    public SOSNode(IGUID guid, String hostname, int port,
                   boolean isClient, boolean isStorage, boolean isDDS) {
        this.nodeGUID = guid;
        this.hostAddress = new InetSocketAddress(hostname, port);

        this.DB_nodeid = guid.toString();
        this.DB_hostname = hostname;
        this.DB_port = port;
        this.DB_is_client = isClient;
        this.DB_is_storage = isStorage;
        this.DB_is_dds = isDDS;
        this.DB_is_nds = false; // FIXME
        this.DB_is_mcs = false; // FIXME

        // TODO - discovery_node
    }

    public SOSNode(SOSConfiguration configuration) throws NodeException {

        try {
            this.nodeGUID = configuration.getNodeGUID();

            String hostname = configuration.getNodeHostname();
            int port = configuration.getNodePort();
            this.hostAddress = new InetSocketAddress(hostname, port);

            // TODO: Have other variables, not the DB ones?
            this.DB_nodeid = nodeGUID.toString();
            this.DB_hostname = hostname;
            this.DB_port = port;
            this.DB_is_client = configuration.nodeIsClient();
            this.DB_is_storage = configuration.nodeIsStorage();
            this.DB_is_dds = configuration.nodeIsDDS();
            this.DB_is_nds = configuration.nodeIsNDS();
            this.DB_is_mcs = configuration.nodeIsMCS();
        } catch (GUIDGenerationException | IOException e) {
            throw new NodeException(e);
        }
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
    public boolean isClient() {
        return DB_is_client;
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
    public boolean isMCS() {
        return DB_is_mcs;
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
