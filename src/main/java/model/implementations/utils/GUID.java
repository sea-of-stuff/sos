package model.implementations.utils;

import java.io.InputStream;

/**
 *
 * TODO - allow multiple algorithms
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class GUID {

    protected byte[] hash;
    protected String hashHex;

    public GUID(InputStream source) {
        hash(source);
    }

    protected abstract void hash(InputStream source);

    public abstract String getAlgorithm();

    public int getHashSize() {
        return hash.length;
    }

    public byte[] getHash() {
        return hash;
    }

    public String getHashHex() {
        return hashHex;
    }
}
