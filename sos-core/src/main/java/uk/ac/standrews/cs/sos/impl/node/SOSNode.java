package uk.ac.standrews.cs.sos.impl.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.json.SOSNodeDeserializer;
import uk.ac.standrews.cs.sos.json.SOSNodeSerializer;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.IP;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = SOSNodeSerializer.class)
@JsonDeserialize(using = SOSNodeDeserializer.class)
public class SOSNode implements Node {

    protected PublicKey signatureCertificate;
    protected IGUID nodeGUID;
    protected InetSocketAddress hostAddress;

    /******************
     * DB fields *
     ******************/
    protected String DB_nodeid;
    private String DB_hostname;
    private int DB_port;
    protected boolean DB_is_agent;
    protected boolean DB_is_storage;
    protected boolean DB_is_dds;
    protected boolean DB_is_nds;
    protected boolean DB_is_mms;
    protected boolean DB_is_cms;
    protected boolean DB_is_rms;

    private SOSNode() {}

    public SOSNode(IGUID guid, PublicKey signatureCertificate, String hostname, int port,
                   boolean isAgent, boolean isStorage, boolean isDDS, boolean isNDS, boolean isMMS, boolean isCMS, boolean isRMS) {

        this.nodeGUID = guid;
        this.signatureCertificate = signatureCertificate;
        this.hostAddress = new InetSocketAddress(hostname, port);

        this.DB_nodeid = guid.toMultiHash();
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

    protected SOSNode(SettingsConfiguration.Settings settings) {

        InetAddress address = IP.findLocalAddress();
        int port = settings.getRest().getPort();
        this.hostAddress = new InetSocketAddress(address, port);

        this.DB_hostname = address.getHostAddress();
        this.DB_port = port;

        this.DB_is_agent = true;
        this.DB_is_storage = settings.getServices().getStorage().isExposed();
        this.DB_is_dds = settings.getServices().getDds().isExposed();
        this.DB_is_nds = settings.getServices().getNds().isExposed();
        this.DB_is_mms = settings.getServices().getMms().isExposed();
        this.DB_is_cms = settings.getServices().getCms().isExposed();
        this.DB_is_rms = settings.getServices().getRms().isExposed();
    }


    // Cloning constructor
    public SOSNode(Node node) {

        this(node.getNodeGUID(), node.getSignatureCertificate(), node.getHostname(), node.getHostAddress().getPort(),
                node.isAgent(), node.isStorage(), node.isDDS(), node.isNDS(), node.isMMS(), node.isCMS(), node.isRMS());
    }

    @Override
    public IGUID getNodeGUID() {

        if (nodeGUID == null) {
            try {
                nodeGUID = GUIDFactory.recreateGUID(DB_nodeid);
            } catch (GUIDGenerationException e) {
                nodeGUID = new InvalidID();
            }
        }

        return nodeGUID;
    }

    @Override
    public PublicKey getSignatureCertificate() {
        return signatureCertificate;
    }

    @Override
    public InetSocketAddress getHostAddress() {

        if (hostAddress == null) {
            hostAddress = new InetSocketAddress(DB_hostname, DB_port);
        }

        return hostAddress;
    }

    @Override
    public String getHostname() {
        return DB_hostname;
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

    @Override
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            SOS_LOG.log(LEVEL.ERROR, "Unable to generate JSON for node" + this.getNodeGUID().toShortString());
            return "";
        }

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
