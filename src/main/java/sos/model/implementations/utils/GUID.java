package sos.model.implementations.utils;

/**
 * Globally Unique Identifier - GUID.
 *
 * TODO - allow multiple algorithms
 * TODO - split class in guid generator and guid.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class GUID {

    protected String hashHex;

    public String toString() {
        return hashHex;
    }

}
