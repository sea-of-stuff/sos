package uk.ac.standrews.cs.sos.network;

import okhttp3.MediaType;

import java.io.InputStream;
import java.net.URL;

/**
 * This is a wrapper class around okhttp.Request
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Request {

    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final MediaType MULTIPART = MediaType.parse("multipart/form-data data; charset=utf-8");

    protected okhttp3.Request request;
    protected Method method;
    protected URL url;
    protected String json_body;
    protected InputStream inputStream;

    public Request(Method method, URL url) {
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
        return request.toString();
    }

}
