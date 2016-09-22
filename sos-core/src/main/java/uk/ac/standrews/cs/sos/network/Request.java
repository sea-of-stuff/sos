package uk.ac.standrews.cs.sos.network;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.utils.LOG;

import java.io.IOException;
import java.net.URL;

/**
 * This is a wrapper class around okhttp.Request
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Request {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    protected okhttp3.Request request;
    protected URL url;
    protected String json_body;

    private Method method;

    public Request(Method method, URL url) {
        this.method = method;
        this.url = url;
    }

    public Request setJSONBody(String json_body) {
        this.json_body = json_body;
        return this;
    }

    public void play(OkHttpClient client) throws IOException {
        LOG.log(LEVEL.INFO, "Play request. Method: " + method + " URL: " + url.toString());

        switch(method) {
            case GET:
                get(client);
                break;
            case POST:
                postJSON(client);
                break;
            case PUT:
                putJSON(client);
                break;
            default:
                LOG.log(LEVEL.WARN, "Unknown Request method while playing a request");
        }
    }

    protected abstract void get(OkHttpClient client) throws IOException;

    protected abstract void postJSON(OkHttpClient client) throws IOException;

    protected abstract void putJSON(OkHttpClient client) throws IOException;

    @Override
    public String toString() {
        return request.toString();
    }

}
