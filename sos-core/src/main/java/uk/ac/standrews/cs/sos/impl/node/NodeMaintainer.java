package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

/**
 * The NodeMaintainer flushes caches/indices/etc
 * The NodeMaintainer deletes all data and manifests that are safe to delete (e.g. content is replicated elsewhere)
 * The NodeMaintainer can be applied as a periodic scheduled thread
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeMaintainer implements Runnable {

    private LocalStorage localStorage;
    private DataDiscoveryService dataDiscoveryService;
    private Storage storage;
    private UsersRolesService usersRolesService;
    private ContextService contextService;

    private long maxSize;

    NodeMaintainer(LocalStorage localStorage, DataDiscoveryService dataDiscoveryService, Storage storage,
                   UsersRolesService usersRolesService, ContextService contextService) {

        this.localStorage = localStorage;
        this.dataDiscoveryService = dataDiscoveryService;
        this.storage = storage;
        this.usersRolesService = usersRolesService;
        this.contextService = contextService;

        maxSize = SOSLocalNode.settings.getGlobal().getNodeMaintainer().getMaxSize();
    }

    @Override
    public void run() {

        flush();

        boolean cleanCache = checkCache();
        if (cleanCache) {
            cleanCache();
        }

        // Run the GC while we are at it...
        System.gc();
    }

    public void flush() {

        dataDiscoveryService.flush();
        storage.flush();
        usersRolesService.flush();
        contextService.flush();
    }

    public void shutdown() {

        contextService.shutdown();
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

    private void cleanCache() {
        SOS_LOG.log(LEVEL.INFO, "Cache Flusher: Work in progress");


        // Remove files that are replicated in at least N places?

        // OLD NOTES
        // Remove unnecessary files - least used files or bigger files
        // Check caches and indices
        // satisfied that data is replicated
    }
}
