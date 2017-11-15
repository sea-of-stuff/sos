package uk.ac.standrews.cs.sos.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * IO Utility methods
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IO {

    public static String InputStreamToString(InputStream input) throws IOException {
        return IOUtils.toString(input, StandardCharsets.ISO_8859_1);
    }

    public static InputStream StringToInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.ISO_8859_1));
    }

    public static String InputStreamToBase64String(InputStream inputStream) throws IOException {

        return new String(Base64.encodeBase64(IOUtils.toByteArray(inputStream)));
    }

    public static InputStream Base64StringToInputStream(String input64) {

        return new ByteArrayInputStream(Base64.decodeBase64(input64));
    }

    public static String toBase64(String input) {

        byte[] bytesEncoded = Base64.encodeBase64(input.getBytes());
        return new String(bytesEncoded);
    }

    public static String fromBase64(String input64) {

        byte[] valueDecoded = Base64.decodeBase64(input64);
        return new String(valueDecoded);
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
