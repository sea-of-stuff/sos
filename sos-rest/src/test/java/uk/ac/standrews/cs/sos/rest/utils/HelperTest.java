package uk.ac.standrews.cs.sos.rest.utils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HelperTest {

    public static InputStream StringToInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String InputStreamToString(InputStream string) throws IOException {
        return IOUtils.toString(string, StandardCharsets.UTF_8);
    }

}
