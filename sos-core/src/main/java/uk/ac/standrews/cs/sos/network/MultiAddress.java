package uk.ac.standrews.cs.sos.network;

/**
 * TODO: implementation of the MultiAddress inspired by the IPFS multiaddress: https://github.com/multiformats/multiaddr
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MultiAddress {

    private String protocol;
    private String host;
    private int port;

    public MultiAddress(String address) {
        // TODO
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return protocol + "//" + host + ":" + port;
    }
}
