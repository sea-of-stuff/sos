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
package uk.ac.standrews.cs.sos.impl.context.directory;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.IgnoreException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.database.DatabaseFactory;
import uk.ac.standrews.cs.sos.impl.database.DatabaseType;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.context.ContextsContentsDirectory;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;

import static uk.ac.standrews.cs.sos.constants.Internals.CMS_INDEX_FILE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextsContentsDirectoryFactory {

    public ContextsContentsDirectory makeContextsContentsDirectory(ContextsContentsDirectoryType type, LocalStorage localStorage) throws ContextException {

        switch(type) {
            case IN_MEMORY:
                return makeContextsContentsDirectoryInMemory(localStorage);
            case DATABASE:
                try {
                    return (ContextsContentsDirectory) DatabaseFactory.instance().getDatabase(DatabaseType.CONTEXTS);
                } catch (DatabaseException e) {
                    throw new ContextException("Unable to make ContextsContentsDirectoryDatabase");
                }
        }

        throw new ContextException("Unable to make a ContextsContentsDirectory");
    }

    private ContextsContentsDirectory makeContextsContentsDirectoryInMemory(LocalStorage localStorage) throws ContextException {

        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            if (cacheDir.contains(CMS_INDEX_FILE)) {

                IFile contextsContentsFile = localStorage.createFile(cacheDir, CMS_INDEX_FILE);
                return (ContextsContentsDirectoryInMemory) Persistence.load(contextsContentsFile);

            } else {
                return new ContextsContentsDirectoryInMemory();
            }

        } catch (DataStorageException | IOException | ClassNotFoundException e) {
            throw new ContextException("ContextService - Unable to load CMS Index");

        } catch (IgnoreException e) {
            SOS_LOG.log(LEVEL.WARN, "Ignore exception on CMS index loading");
            return new ContextsContentsDirectoryInMemory();
        }
    }
}
