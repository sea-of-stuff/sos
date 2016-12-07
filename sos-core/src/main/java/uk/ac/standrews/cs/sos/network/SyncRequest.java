package uk.ac.standrews.cs.sos.network;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SyncRequest extends Request {

    private Response response;

    public SyncRequest(Method method, URL url) {
        super(method, url);
    }

    public Response play(OkHttpClient client) throws IOException {
        SOS_LOG.log(LEVEL.INFO, "Play request. Method: " + method + " URL: " + url.toString());

        Response response;
        switch(method) {
            case GET: response = get(client); break;
            case POST: response = managePOST(client); break;
            case PUT: response = managePUT(client); break;
            default:
                SOS_LOG.log(LEVEL.ERROR, "Unknown Request method while playing a request");
                throw new IOException("Unknown Request method");
        }

        return response;
    }

    private Response managePOST(OkHttpClient client) throws IOException {

        Response response;
        if (inputStream != null) {
            response = postData(client);
        } else if (json_body != null) {
            response = postJSON(client);
        } else {
            throw new IOException("No body to post");
        }

        return response;
    }

    private Response managePUT(OkHttpClient client) throws IOException {

        Response response;
        if (json_body != null) {
            response = putJSON(client);
        } else {
            throw new IOException("No body to post");
        }

        return response;
    }

    protected Response get(OkHttpClient client) throws IOException {

        request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        response = new Response(client.newCall(request).execute());
        return response;
    }

    private Response postJSON(OkHttpClient client) throws IOException {
        RequestBody body = RequestBody.create(JSON, json_body);

        request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        response = new Response(client.newCall(request).execute());
        return response;
    }

    private Response postData(OkHttpClient client) throws IOException {
        // FIXME - this will most likely fail for large data
        byte[] bytes = IOUtils.toByteArray(inputStream);
        RequestBody body = RequestBody.create(MULTIPART, bytes);

        request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        response = new Response(client.newCall(request).execute());
        return response;
    }

    private Response putJSON(OkHttpClient client) throws IOException {
        RequestBody body = RequestBody.create(JSON, json_body);

        request = new okhttp3.Request.Builder()
                .url(url)
                .put(body)
                .build();

        response = new Response(client.newCall(request).execute());
        return response;
    }

    public int getRespondeCode() {
        return response.getCode();
    }

    public Response getResponse() {
        return response;
    }
}
