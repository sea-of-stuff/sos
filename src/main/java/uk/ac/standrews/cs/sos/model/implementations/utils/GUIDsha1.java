package uk.ac.standrews.cs.sos.model.implementations.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;

import java.io.InputStream;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GUIDsha1 extends GUID {

    private static final String ALGORITHM = "sha-1";

    public GUIDsha1(String guid) {
        this.hashHex = guid;
    }

    public GUIDsha1(InputStream source) throws GuidGenerationException {
        hash(source);
    }

    protected void hash(InputStream source) throws GuidGenerationException {
        try {
            byte[] hash = DigestUtils.sha1(source);
            hashHex = Hex.encodeHexString(hash);
        } catch (Exception e) {
            throw new GuidGenerationException("InputStream could not be hashed");
        }
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String toString() {
        return hashHex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GUIDsha1 guid = (GUIDsha1) o;
        return Objects.equals(hashHex, guid.hashHex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashHex);
    }
}
