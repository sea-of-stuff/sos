package IO.utils;

import org.apache.xmlbeans.impl.common.ReaderInputStream;

import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StreamsUtils {

    public static InputStream StringToInputStream(String input) throws UnsupportedEncodingException {
        StringReader reader = new StringReader(input);
        InputStream inputStream = new ReaderInputStream(reader, "UTF-8");

        return inputStream;
    }
}
