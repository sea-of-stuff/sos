package uk.ac.standrews.cs.sos.json.model;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.net.InetSocketAddress;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeModel implements Node {

    private boolean isClient;
    private boolean isStorage;
    private boolean isDDS;
    private boolean isNDS;
    private boolean isMCS;

    private String guid;
    private String ip;
    private int port;

    public NodeModel() {}

    @Override
    public IGUID getNodeGUID() {

        try {
            return GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public InetSocketAddress getHostAddress() {
        return new InetSocketAddress(ip, port);
    }

    @Override
    public boolean isClient() {
        return isClient;
    }

    public void setClient(boolean client) {
        isClient = client;
    }

    @Override
    public boolean isStorage() {
        return isStorage;
    }

    public void setStorage(boolean storage) {
        isStorage = storage;
    }

    @Override
    public boolean isDDS() {
        return isDDS;
    }

    public void setDDS(boolean DDS) {
        isDDS = DDS;
    }

    @Override
    public boolean isNDS() {
        return isNDS;
    }

    public void setNDS(boolean NDS) {
        isNDS = NDS;
    }

    @Override
    public boolean isMCS() {
        return isMCS;
    }

    public void setMCS(boolean MCS) {
        isMCS = MCS;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
}
