package uk.ac.standrews.cs.sos.impl.network;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AsyncRequest extends Request {

    private ResponseCallback callback;

    public AsyncRequest(HTTPMethod method, URL url, ResponseCallback callback) {
        super(method, url);
        this.callback = callback;
    }

    public void play(OkHttpClient client) throws IOException {
        SOS_LOG.log(LEVEL.INFO, "Play request. Method: " + method + " URL: " + url.toString());

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
                SOS_LOG.log(LEVEL.WARN, "Unknown Request method while playing a request");
        }
    }

    protected void get(OkHttpClient client) throws IOException {
        request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    protected void postJSON(OkHttpClient client) throws IOException {
        RequestBody body = RequestBody.create(JSON, json_body);

        request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    protected void putJSON(OkHttpClient client) throws IOException {
        RequestBody body = RequestBody.create(JSON, json_body);

        new okhttp3.Request.Builder()
                .url(url)
                .put(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

}
