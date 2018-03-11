package uk.ac.standrews.cs.sos.services;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Service {

    /**
     * Flushes the in-memory caches and indices into disk
     */
    void flush();

    /**
     * Stops the threads and cleans up the data structures
     */
    void shutdown();
}
