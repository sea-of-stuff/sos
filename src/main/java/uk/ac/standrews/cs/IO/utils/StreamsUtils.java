package uk.ac.standrews.cs.IO.utils;

import org.apache.xmlbeans.impl.common.ReaderInputStream;

import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * Collection of utilities for input/output streams.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StreamsUtils {

    // Suppresses default constructor, ensuring non-instantiability.
    private StreamsUtils() {}

    public static InputStream StringToInputStream(String input) throws UnsupportedEncodingException {
        StringReader reader = new StringReader(input);
        return new ReaderInputStream(reader, "UTF-8");
    }
}
