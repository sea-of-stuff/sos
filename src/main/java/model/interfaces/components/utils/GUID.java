package model.interfaces.components.utils;

/**
 * Globally Unique Identifier - it is a fixed length unique identifier.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface GUID {

    /**
     * @return Algorithm used for this GUID
     * See <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest">Standard Names - Message Digest</a>
     */
    String getAlgorithm();

    /**
     * @return Size of the GUID in bytes
     */
    int getHashSize();

    /**
     * @return this GUID in bytes
     */
    byte[] getHash();
}
