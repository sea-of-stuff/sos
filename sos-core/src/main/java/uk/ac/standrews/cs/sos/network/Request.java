package uk.ac.standrews.cs.sos.network;


import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Request {

    public static final String SOS_NODE_CHALLENGE_HEADER = "sos-node-challenge";

    protected HTTPMethod method;
    protected URL url;
    protected String json_body;
    protected InputStream inputStream;

    protected PublicKey signatureCertificate;
    protected String nodeChallenge;

    public Request(HTTPMethod method, URL url) {
        this.method = method;
        this.url = url;
    }

    public Request(PublicKey signatureCertificate, HTTPMethod method, URL url) {
        this.method = method;
        this.url = url;

        this.signatureCertificate = signatureCertificate;
        SecureRandom random = new SecureRandom();
        this.nodeChallenge = new BigInteger(1024, random).toString(32);
    }

    public Request setJSONBody(String json_body) {
        this.json_body = json_body;
        this.inputStream = null;
        return this;
    }

    public Request setBody(InputStream inputStream) {
        this.inputStream = inputStream;
        this.json_body = null;
        return this;
    }

    @Override
    public String toString() {
        return method.toString() + " " + url.toString();
    }

}
