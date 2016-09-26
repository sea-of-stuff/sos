package uk.ac.standrews.cs.sos.network;

import java.io.InputStream;

/**
 * Wrapper around the okhttp3 response class
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Response {

    private okhttp3.Response response;

    public Response(okhttp3.Response response) {
        this.response = response;
    }

    public int getCode() {
        return response.code();
    }

    public InputStream getBody() {
        return response.body().byteStream();
    }
}
