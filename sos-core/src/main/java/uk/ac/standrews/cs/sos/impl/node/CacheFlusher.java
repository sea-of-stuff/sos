package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

/**
 * The Cache Flusher deletes all data and manifests that are safe to delete (e.g. content is replicated elsewhere)
 * The Cache Flusher can be apply as a periodic scheduled thread
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheFlusher implements Runnable {

    private LocalStorage localStorage;
    private long maxSize;

    public CacheFlusher(LocalStorage localStorage) {
        this.localStorage = localStorage;

        maxSize = SOSLocalNode.settings.getGlobal().getCacheFlusher().getMaxSize();
    }

    @Override
    public void run() {

        boolean runFlusher = checkCache();
        if (runFlusher) {
            flush();
        }

        // Run the GC while we are at it...
        System.gc();
    }

    /**
     *
     * @return true if garbage collection condition is satisfied
     */
    private boolean checkCache() {

        try {
            IDirectory datDir = localStorage.getDataDirectory();
            long dataSize = datDir.getSize();
            SOS_LOG.log(LEVEL.INFO, "Cache Flusher: Data Directory size is: " + dataSize);

            return dataSize > maxSize;
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
        // satisfied that data is replicated
    }
}
