package uk.ac.standrews.cs.sos.impl.network;


import java.io.InputStream;
import java.net.URL;

/**
 * This is a wrapper class around okhttp.Request
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Request {

    protected HTTPMethod method;
    protected URL url;
    protected String json_body;
    protected InputStream inputStream;

    public Request(HTTPMethod method, URL url) {
        this.method = method;
        this.url = url;
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
