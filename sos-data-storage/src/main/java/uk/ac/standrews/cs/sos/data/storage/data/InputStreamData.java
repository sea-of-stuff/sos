/*
 * Created on June 24, 2005 at 10:51:17 AM.
 */
package uk.ac.standrews.cs.sos.storage.data;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IData implementation that gets data from an input stream.
 *
 * @author al
 */
public class InputStreamData implements Data {

    private static final Logger log = Logger.getLogger(InputStreamData.class.getName());

    private byte[] state;

    public InputStreamData(InputStream inputStream) {
        // TODO - do not convert it to a byte array. This could be used for large chunks of data!
        try {
            state = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.log(Level.SEVERE, "IO Exception during stream read", e);
        }
    }

    /**
     * Creates an instance using a given stream.
     *
     * @param inputStream a stream containing the underlying data
     */
    public InputStreamData(InputStream inputStream, int expected_byte_count) {

        state = new byte[expected_byte_count];
        int bytes_read = 0;

        try {
            int available = 0;
            do {
                available = inputStream.available();
                if (available > expected_byte_count) {
                    available = expected_byte_count;
                }
                int read = inputStream.read(state, bytes_read, available);
                bytes_read += read;
            }
            while (bytes_read != expected_byte_count && available != 0); // TODO Al - need to protect against erroneous clients
        } catch (IOException e) {
            log.log(Level.SEVERE, "IO Exception during stream read", e);
        }

        log.log(Level.FINE, "Total data read in bytes: " + bytes_read);
    }

    /**
     * Gets the data.
     *
     * @return the underlying data
     */
    public byte[] getState() {
        return state;
    }

    /**
     * Gets the size of the data in bytes.
     *
     * @return the size of the data
     */
    public long getSize() {
        return state.length;
    }

    /**
     * Creates an input stream reading from the byte array.
     *
     * @return an input stream reading from the byte array
     */
    public InputStream getInputStream() {
        return new ByteArrayInputStream(state);
    }

    /**
     * Tests equality with another instance.
     *
     * @return true if the array's contents are equivalent to those of the given array
     * @see Object#equals(Object)
     */
    public boolean equals(Object o) {
        return o instanceof Data && Arrays.equals(getState(), ((Data) (o)).getState());
    }

    public String toString() {
        return new String(state);
    }
}
