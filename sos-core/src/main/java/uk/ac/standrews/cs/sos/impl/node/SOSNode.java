package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.utilities.network.NetworkUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNode extends BasicManifest implements Node {

    protected IGUID nodeGUID; // derived by signature certificate.

    PublicKey signatureCertificate;
    InetSocketAddress hostAddress;

    /******************
     * DB fields *
     ******************/
    private String DB_nodeid;
    private String DB_hostname;
    private int DB_port;
    private boolean DB_is_agent;
    private boolean DB_is_storage;
    private boolean DB_is_mds;
    private boolean DB_is_nds;
    private boolean DB_is_mms;
    private boolean DB_is_cms;
    private boolean DB_is_rms;
    private boolean DB_is_experiment;

    public SOSNode(IGUID guid, PublicKey signatureCertificate, String hostname, int port,
                   boolean isAgent, boolean isStorage, boolean isMDS, boolean isNDS, boolean isMMS, boolean isCMS, boolean isRMS, boolean isExperiment) {
        super(ManifestType.NODE);

        this.nodeGUID = guid;
        this.signatureCertificate = signatureCertificate;
        this.hostAddress = new InetSocketAddress(hostname, port);

        this.DB_nodeid = guid.toMultiHash();
        this.DB_hostname = hostname;
        this.DB_port = port;
        this.DB_is_agent = isAgent;
        this.DB_is_storage = isStorage;
        this.DB_is_mds = isMDS;
        this.DB_is_nds = isNDS;
        this.DB_is_mms = isMMS;
        this.DB_is_cms = isCMS;
        this.DB_is_rms = isRMS;
        this.DB_is_experiment = isExperiment;
    }

    protected SOSNode(SettingsConfiguration.Settings settings) throws SOSException {
        super(ManifestType.NODE);

        InetAddress address = getLocalAddress();
        int port = settings.getRest().getPort();
        this.hostAddress = new InetSocketAddress(address, port);

        this.DB_hostname = address.getHostAddress();
        this.DB_port = port;

        this.DB_is_agent = true;
        this.DB_is_storage = settings.getServices().getStorage().isExposed();
        this.DB_is_mds = settings.getServices().getMds().isExposed();
        this.DB_is_nds = settings.getServices().getNds().isExposed();
        this.DB_is_mms = settings.getServices().getMms().isExposed();
        this.DB_is_cms = settings.getServices().getCms().isExposed();
        this.DB_is_rms = settings.getServices().getRms().isExposed();
        this.DB_is_experiment = settings.getServices().getExperiment().isExposed();
    }

    // Cloning constructor
    public SOSNode(Node node) {

        this(node.guid(), node.getSignatureCertificate(), node.getIP(), node.getHostAddress().getPort(),
                node.isAgent(), node.isStorage(), node.isMDS(), node.isNDS(), node.isMMS(), node.isCMS(), node.isRMS(), node.isExperiment());
    }

    @Override
    public IGUID guid() {

        if (nodeGUID == null) {
            try {
                nodeGUID = GUIDFactory.recreateGUID(DB_nodeid);
            } catch (GUIDGenerationException e) {
                nodeGUID = new InvalidID();
            }
        }

        return nodeGUID;
    }

    void setDB_NODEID(String db_nodeid) {
        this.DB_nodeid = db_nodeid;
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
    public String getIP() {
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
    public boolean isMDS() {
        return DB_is_mds;
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
    public boolean isExperiment() {
        return DB_is_experiment;
    }

    @Override
    public InputStream contentToHash() {

        return new ByteArrayInputStream(signatureCertificate.getEncoded());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SOSNode sosNode = (SOSNode) o;
        return Objects.equals(guid(), sosNode.guid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid());
    }

    private InetAddress getLocalAddress() throws SOSException {

        try {
            InetAddress address = NetworkUtil.getLocalIPv4Address();
            assert(address != null);

            return address;

        } catch (UnknownHostException e) {
            throw new SOSException("Unable to establish IP address for node");
        }
    }

}
