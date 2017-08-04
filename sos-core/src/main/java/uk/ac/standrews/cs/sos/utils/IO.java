package uk.ac.standrews.cs.sos.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * IO Utility methods
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IO {

    public static String InputStreamToString(InputStream string) throws IOException {
        return IOUtils.toString(string, StandardCharsets.UTF_8);
    }

    public static InputStream StringToInputStream(String input) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    public static ByteArrayOutputStream InputStreamToByteArrayOutputStream(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(input, baos);
        return baos;
    }

    public static InputStream OutputStreamToInputStream(ByteArrayOutputStream out) throws IOException {

        return new ByteArrayInputStream(out.toByteArray());
    }

    public static InputStream CloneInputStream(InputStream inputStream) throws IOException {

        try (final ByteArrayOutputStream baos = IO.InputStreamToByteArrayOutputStream(inputStream)) {
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }

}
