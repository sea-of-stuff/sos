package uk.ac.standrews.cs.sos.model.implementations.utils;

import org.apache.xmlbeans.impl.common.ReaderInputStream;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * Globally Unique Identifier - GUID.
 *
 * TODO - allow multiple algorithms
 * TODO - split class in guid generator and guid.
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
        } catch (UnsupportedEncodingException e) {
            throw new GuidGenerationException("Unsupported Encoding");
        } catch (IOException e) {
            throw new GuidGenerationException("uk.ac.standrews.cs.IO Exception");
        } catch (Exception e) {
            throw new GuidGenerationException("General Exception");
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
