package uk.ac.standrews.cs.sos.impl.context.directory;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.database.DatabaseFactory;
import uk.ac.standrews.cs.sos.impl.database.DatabaseType;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.context.ContextsContentsDirectory;
import uk.ac.standrews.cs.sos.utils.Persistence;

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
        }
    }
}
