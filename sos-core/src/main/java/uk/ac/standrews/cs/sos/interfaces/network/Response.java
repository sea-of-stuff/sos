package uk.ac.standrews.cs.sos.interfaces.network;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Response {

    int getCode();

    InputStream getBody();
}
