package model.implementations.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GUIDsha1 extends GUID {

    private static final String ALGORITHM = "sha-1";

    public GUIDsha1(InputStream source) {
        super(source);
    }

    protected void hash(InputStream source) {
        try {
            hash = DigestUtils.sha1(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }

}
