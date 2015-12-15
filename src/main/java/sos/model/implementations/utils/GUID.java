package sos.model.implementations.utils;

import sos.exceptions.GuidGenerationException;

import java.io.InputStream;

/**
 * Globally Unique Identifier - GUID.
 *
 * TODO - allow multiple algorithms
 * TODO - split class in guid generator and guid.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class GUID {

    protected byte[] hash;
    protected String hashHex;

    public GUID(InputStream source) throws GuidGenerationException {
        hash(source);
    }

    protected abstract void hash(InputStream source) throws GuidGenerationException;

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
