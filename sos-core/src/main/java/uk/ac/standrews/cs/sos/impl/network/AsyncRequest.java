package uk.ac.standrews.cs.sos.impl.network;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.net.URL;

/**
 * TODO - the async request should be re-implemented using Unirest
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AsyncRequest extends Request {

    private ResponseCallback callback;

    public AsyncRequest(HTTPMethod method, URL url, ResponseCallback callback) {
        super(method, url);
        this.callback = callback;
    }

    public void play() throws IOException {
        SOS_LOG.log(LEVEL.INFO, "Play request. Method: " + method + " URL: " + url.toString());

        switch(method) {
            case GET:
                get();
                break;
            case POST:
                postJSON();
                break;
            case PUT:
                putJSON();
                break;
            default:
                SOS_LOG.log(LEVEL.WARN, "Unknown Request method while playing a request");
        }
    }

    protected void get() throws IOException {
//        request = new okhttp3.Request.Builder()
//                .url(url)
//                .build();
//
//        client.newCall(request).enqueue(callback);
    }

    protected void postJSON() throws IOException {
//        RequestBody body = RequestBody.create(JSON, json_body);
//
//        request = new okhttp3.Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(callback);
    }

    protected void putJSON() throws IOException {
//        RequestBody body = RequestBody.create(JSON, json_body);
//
//        new okhttp3.Request.Builder()
//                .url(url)
//                .put(body)
//                .build();
//
//        client.newCall(request).enqueue(callback);
    }

}
