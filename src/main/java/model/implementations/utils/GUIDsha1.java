package model.implementations.utils;

import model.exceptions.GuidGenerationException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GUIDsha1 extends GUID {

    private static final String ALGORITHM = "sha-1";

    public GUIDsha1(InputStream source) throws GuidGenerationException {
        super(source);
    }

    protected void hash(InputStream source) throws GuidGenerationException {
        try {
            hash = DigestUtils.sha1(source);
            hashHex = Hex.encodeHexString(hash);
        } catch (Exception e) {
            throw new GuidGenerationException("InputStream could not be hashed");
        }
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }

    public String toString() {
        return hashHex;
    }

}
