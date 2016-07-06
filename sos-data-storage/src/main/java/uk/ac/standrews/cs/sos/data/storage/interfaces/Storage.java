package uk.ac.standrews.cs.sos.storage.interfaces;

import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.exceptions.DestroyException;

import java.io.IOException;

/**
 * This interface allow SOS implementations to interact with different
 * types of storage implementations
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Storage {

    boolean isImmutable();

    /**
     * Get the root directory of this storage
     * @return
     */
    Directory getRoot();

    /**
     * Get the directory where data is stored
     * @return
     */
    Directory getDataDirectory() throws IOException;

    /**
     * Get the directory where manifests are stored
     * @return
     */
    Directory getManifestDirectory() throws IOException;

    /**
     * Get a directory that can be used for any test purposes
     * @return
     */
    Directory getTestDirectory() throws IOException;

    /**
     * Create a directory within the specified parent and with the given name
     * @param parent
     * @param name
     * @return
     */
    Directory createDirectory(Directory parent, String name) throws IOException;

    /**
     * Create a directory at the root of this storage with the given name
     * @param name
     * @return
     */
    Directory createDirectory(String name) throws IOException;

    /**
     * Create a file a the specified parent directory
     * @param parent
     * @param filename
     * @return
     */
    File createFile(Directory parent, String filename) throws IOException;

    File createFile(Directory parent, String filename, Data data) throws IOException;

    void destroy() throws DestroyException;
}
