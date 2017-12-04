package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.InternetProtocol;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNode extends BasicManifest implements Node {

    protected PublicKey signatureCertificate;
    protected IGUID nodeGUID; // derived by signature certificate.
    protected InetSocketAddress hostAddress;

    /******************
     * DB fields *
     ******************/
    private String DB_nodeid;
    private String DB_hostname;
    private int DB_port;
    private boolean DB_is_agent;
    private boolean DB_is_storage;
    private boolean DB_is_dds;
    private boolean DB_is_nds;
    private boolean DB_is_mms;
    private boolean DB_is_cms;
    private boolean DB_is_rms;

    public SOSNode(IGUID guid, PublicKey signatureCertificate, String hostname, int port,
                   boolean isAgent, boolean isStorage, boolean isDDS, boolean isNDS, boolean isMMS, boolean isCMS, boolean isRMS) {
        super(ManifestType.NODE);

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
        super(ManifestType.NODE);

        InetAddress address = InternetProtocol.findLocalAddress();
        assert(address != null);

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

        this(node.guid(), node.getSignatureCertificate(), node.getIP(), node.getHostAddress().getPort(),
                node.isAgent(), node.isStorage(), node.isDDS(), node.isNDS(), node.isMMS(), node.isCMS(), node.isRMS());
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

}
