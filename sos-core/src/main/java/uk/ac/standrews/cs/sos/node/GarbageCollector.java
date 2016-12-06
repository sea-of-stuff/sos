package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.interfaces.Directory;

import java.util.concurrent.TimeUnit;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GarbageCollector implements Runnable {

    protected static final int PERIOD = 10;
    protected static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private static final long ONE_MB = 1024L * 1024L;
    private static final long DATA_SIZE_LIMIT = 1L * ONE_MB;

    private LocalStorage localStorage;

    public GarbageCollector(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    @Override
    public void run() {

        boolean runGC = checkGCCondition();
        if (runGC) {
            gc();
        }
    }

    /**
     *
     * @return true if garbage collection condition is satisfied
     */
    private boolean checkGCCondition() {

        try {
            Directory datDir = localStorage.getDataDirectory();
            long dataSize = datDir.getSize();
            System.out.println("size is: " + dataSize);
            return dataSize > DATA_SIZE_LIMIT;
        } catch (DataStorageException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void gc() {
        // Remove unnecessary files
        // Check caches and indices
        System.out.println("garbage collecting - WORK IN PROGRESS");
    }
}
