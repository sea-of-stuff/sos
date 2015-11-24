package model.implementations.utils;

import IO.sources.DataSource;

/**
 *
 * TODO - allow multiple algorithms
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class GUID {

    protected byte[] hash;

    public GUID(DataSource source) {
        hash(source);
    }

    protected abstract void hash(DataSource source);

    public abstract String getAlgorithm();

    public int getHashSize() {
        return hash.length;
    }

    public byte[] getHash() {
        return hash;
    }
}
