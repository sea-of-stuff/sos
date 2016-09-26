package uk.ac.standrews.cs.sos.network;

import okhttp3.MediaType;

import java.net.URL;

/**
 * This is a wrapper class around okhttp.Request
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Request {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    protected okhttp3.Request request;
    protected Method method;
    protected URL url;
    protected String json_body;

    public Request(Method method, URL url) {
        this.method = method;
        this.url = url;
    }

    public Request setJSONBody(String json_body) {
        this.json_body = json_body;
        return this;
    }

    @Override
    public String toString() {
        return request.toString();
    }

}
