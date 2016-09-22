package uk.ac.standrews.cs.sos.network;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    @Override
    protected void get(OkHttpClient client) throws IOException {

        request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        response = client.newCall(request).execute();
    }

    @Override
    protected void postJSON(OkHttpClient client) throws IOException {
        RequestBody body = RequestBody.create(JSON, json_body);

        request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        response = client.newCall(request).execute();
    }

    @Override
    protected void putJSON(OkHttpClient client) throws IOException {
        RequestBody body = RequestBody.create(JSON, json_body);

        request = new okhttp3.Request.Builder()
                .url(url)
                .put(body)
                .build();

        response = client.newCall(request).execute();
    }

    public int getRespondeCode() {
        return response.code();
    }

    public Response getResponse() {
        return response;
    }
}
