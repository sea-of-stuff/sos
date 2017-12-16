package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.PublicKey;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicNode extends BasicManifest implements Node {

    private String ip;
    private InetSocketAddress hostname;

    public BasicNode(String ip, int port) {
        super(ManifestType.NODE);

        guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        this.ip = ip;
        this.hostname = new InetSocketAddress(ip, port);
    }

    @Override
    public InputStream contentToHash() throws IOException {
        return new StringData("").getInputStream();
    }

    @Override
    public PublicKey getSignatureCertificate() {
        return null;
    }

    @Override
    public InetSocketAddress getHostAddress() {
        return hostname;
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public boolean isStorage() {
        return false;
    }

    @Override
    public boolean isDDS() {
        return false;
    }

    @Override
    public boolean isNDS() {
        return false;
    }

    @Override
    public boolean isMMS() {
        return false;
    }

    @Override
    public boolean isCMS() {
        return false;
    }

    @Override
    public boolean isRMS() {
        return false;
    }
}
