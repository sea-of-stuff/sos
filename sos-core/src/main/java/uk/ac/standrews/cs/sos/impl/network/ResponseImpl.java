package uk.ac.standrews.cs.sos.impl.network;

import uk.ac.standrews.cs.sos.interfaces.network.Response;

import java.io.InputStream;

/**
 * Wrapper around the okhttp3 response class
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ResponseImpl implements Response {

    private okhttp3.Response response;

    public ResponseImpl(okhttp3.Response response) {
        this.response = response;
    }

    @Override
    public int getCode() {
        return response.code();
    }

    @Override
    public InputStream getBody() {
        return response.body().byteStream();
    }
}
