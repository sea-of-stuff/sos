package model.implementations.utils;

import IO.sources.DataSource;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GUIDsha1 extends GUID {

    private static final String ALGORITHM = "sha-1";

    public GUIDsha1(DataSource source) {
        super(source);
    }

    protected void hash(DataSource source) {
        try {
            hash = DigestUtils.sha1(source.getStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }

}
