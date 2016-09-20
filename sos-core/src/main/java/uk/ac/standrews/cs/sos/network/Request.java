package uk.ac.standrews.cs.sos.network;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.utils.LOG;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Request {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Method method;
    private String json_body;

    private Response response;

    public Request(Method method) {
        this.method = method;
    }

    public void play(OkHttpClient client) throws IOException {
        // TODO

        switch(method) {
            case GET:
                playGET(client);
                break;
            case POST:
                playPOST(client);
                break;
            default:
                LOG.log(LEVEL.WARN, "Unknown Request method while playing a request");
        }
    }

    private void playGET(OkHttpClient client) throws IOException {
        this.response = get(client, "");
    }

    private void playPOST(OkHttpClient client) {

    }

    Response get(OkHttpClient client, String url) throws IOException {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        return client.newCall(request).execute();
    }

    String postJSON(OkHttpClient client, String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public int getRespondeCode() {
        return response.code();
    }

    public Response getResponse() {
        return response;
    }

}
