package uk.ac.standrews.cs.sos.impl.network;

import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ErrorResponseImpl implements Response {
    @Override
    public int getCode() {
        return 500;
    }

    @Override
    public InputStream getBody() {
        try {
            return IO.StringToInputStream("");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
