/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.StorageService;
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
    private ManifestsDataService manifestsDataService;
    private StorageService storageService;
    private UsersRolesService usersRolesService;
    private ContextService contextService;

    private long maxSize;

    NodeMaintainer(LocalStorage localStorage, ManifestsDataService manifestsDataService, StorageService storageService,
                   UsersRolesService usersRolesService, ContextService contextService) {

        this.localStorage = localStorage;
        this.manifestsDataService = manifestsDataService;
        this.storageService = storageService;
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

    }

    public void flush() {

        manifestsDataService.flush();
        storageService.flush();
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
