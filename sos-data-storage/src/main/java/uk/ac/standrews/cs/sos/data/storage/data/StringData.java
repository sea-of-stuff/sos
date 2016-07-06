package uk.ac.standrews.cs.sos.storage.data;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * IData implementation using a string.
 *
 * @author al
 */
public class StringData implements Data {

    private String state;

    /**
     * Creates an instance using a given string.
     *
     * @param state a string containing the underlying data
     */
    public StringData(String state) {
        this.state = state;
    }

    /**
     * Gets the data.
     *
     * @return the underlying data
     */
    public byte[] getState() {
        return state.getBytes();
    }

    /**
     * Gets the size of the data in bytes.
     *
     * @return the size of the data
     */
    public long getSize() {
        return state.length();
    }

    /**
     * Creates an input stream reading from the string.
     *
     * @return an input stream reading from the string
     */
    public InputStream getInputStream() throws FileNotFoundException {
        return new ByteArrayInputStream(state.getBytes());
    }

    /**
     * Tests equality with another instance.
     *
     * @return true if the string's contents are equivalent to those of the given string
     * @see Object#equals(Object)
     */
    public boolean equals( Object o ) {
        return o instanceof Data && Arrays.equals( getState(), ((Data)(o)).getState() );
    }

    public String toString() {
        return state;
    }
}

