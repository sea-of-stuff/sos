package uk.ac.standrews.cs.sos.git_to_sos;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Blob extends Entity {

    InputStream getData();
}
