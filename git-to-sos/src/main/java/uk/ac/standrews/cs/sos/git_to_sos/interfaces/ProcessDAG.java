package uk.ac.standrews.cs.sos.git_to_sos.interfaces;

import java.io.InputStream;

/**
 * Generic DAG for VCS.
 *
 * Implementations: git itself, sos, etc..
 */
public interface ProcessDAG {

    /**
     * Blobs, atoms
     */
    void addData(InputStream inputStream);

    /**
     * Tree, compound
     * @param pathnames of blob and other trees
     */
    void makeCollection(String[] pathnames);

    /**
     * Commit current tree
     * @param message - will be added as metadata
     */
    void commit(String message);
}
