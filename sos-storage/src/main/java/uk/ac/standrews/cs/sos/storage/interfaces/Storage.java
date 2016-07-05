package uk.ac.standrews.cs.sos.storage.interfaces;

import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.exceptions.DestroyException;

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
    Directory getDataDirectory();

    /**
     * Get the directory where manifests are stored
     * @return
     */
    Directory getManifestDirectory();

    /**
     * Get a directory that can be used for any test purposes
     * @return
     */
    Directory getTestDirectory();

    /**
     * Create a directory within the specified parent and with the given name
     * @param parent
     * @param name
     * @return
     */
    Directory createDirectory(Directory parent, String name);

    /**
     * Create a directory at the root of this storage with the given name
     * @param name
     * @return
     */
    Directory createDirectory(String name);

    /**
     * Create a file a the specified parent directory
     * @param parent
     * @param filename
     * @return
     */
    File createFile(Directory parent, String filename);

    File createFile(Directory parent, String filename, Data data);

    void destroy() throws DestroyException;
}
