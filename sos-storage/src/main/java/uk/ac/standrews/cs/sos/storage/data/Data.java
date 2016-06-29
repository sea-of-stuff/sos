package uk.ac.standrews.cs.sos.storage.data;

import java.io.IOException;
import java.io.InputStream;

/**
 * Representation of an object's state.
 *
 * @author al, graham
 */
public interface Data {

    /**
     * Gets the object's state.
     *
     * @return the underlying data
     */
    byte[] getState();

    /**
     * Gets the size of the object in bytes.
     *
     * @return the size of the data
     */
    long getSize();

    /**
     * Creates an input stream reading from the object's state.
     *
     * @return an input stream reading from the object's state
     * @throws IOException if the object's state could not be read
     */
    InputStream getInputStream() throws IOException;

    /**
     * Tests equality with another instance.
     *
     * @param o the instance to be compared
     * @return true if the object's contents are equivalent to those of the given object
     * @see Object#equals(Object)
     */
    boolean equals(Object o);
}

