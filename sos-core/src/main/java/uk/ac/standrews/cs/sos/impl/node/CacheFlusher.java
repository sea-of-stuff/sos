package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.interfaces.Directory;

import static uk.ac.standrews.cs.sos.constants.Internals.CACHE_DATA_SIZE_LIMIT;

/**
 * The Cache Flusher deletes all data and manifests that are safe to delete (e.g. content is replicated elsewhere)
 * The Cache Flusher can be run as a periodic scheduled thread
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheFlusher implements Runnable {

    private LocalStorage localStorage;

    public CacheFlusher(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    public void run() {

        boolean runFlusher = checkCache();
        if (runFlusher) {
            flush();
        }
    }

    /**
     *
     * @return true if garbage collection condition is satisfied
     */
    private boolean checkCache() {

        try {
            Directory datDir = localStorage.getDataDirectory();
            long dataSize = datDir.getSize();
            SOS_LOG.log(LEVEL.INFO, "Cache Flusher: Data Directory size is: " + dataSize);

            return dataSize > CACHE_DATA_SIZE_LIMIT;
        } catch (DataStorageException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void flush() {
        SOS_LOG.log(LEVEL.INFO, "Cache Flusher: Work in progress");


        // Remove files that are replicated in at least N places?

        // OLD NOTES
        // Remove unnecessary files - least used files or bigger files
        // Check caches and indices
        // check that data is replicated
    }
}
