package uk.ac.standrews.cs.utils;

import org.apache.xmlbeans.impl.common.ReaderInputStream;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Globally Unique Identifier - GUID.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class GUID {

    protected String hashHex;

    public String toString() {
        return hashHex;
    }

    public static GUID generateGUID(String string) throws GuidGenerationException {
        GUID guid;
        try (StringReader reader = new StringReader(string);
             InputStream inputStream = new ReaderInputStream(reader, "UTF-8")) {

            guid = generateGUID(inputStream);
        } catch (IOException e) {
            throw new GuidGenerationException();
        } catch (Exception e) {
            throw new GuidGenerationException();
        }

        return guid;
    }

    public static GUID generateGUID(InputStream inputStream) throws GuidGenerationException {
        return new GUIDsha1(inputStream);
    }

    public static GUID generateRandomGUID() throws GuidGenerationException {
        return GUID.generateGUID(Double.toString(Math.random()));
    }

}
