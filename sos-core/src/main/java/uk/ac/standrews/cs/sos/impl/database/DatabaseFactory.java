package uk.ac.standrews.cs.sos.impl.database;

import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.interfaces.database.Database;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DatabaseFactory {

    private NodesDatabase nodesDatabase;
    private TasksDatabase tasksDatabase;
    private ContextsContentsDirectoryDatabase contextsContentsDirectoryDatabase;

    private DatabaseFactory(IFile dbFile) throws DatabaseException {

        nodesDatabase = new NodesDatabaseImpl(dbFile);
        tasksDatabase = new TasksDatabase(dbFile);
        contextsContentsDirectoryDatabase = new ContextsContentsDirectoryDatabase(dbFile);
    }

    private static DatabaseFactory instance;

    public static void initInstance(IFile dbFile) throws DatabaseException {

        boolean dbExists = dbFile.exists();
        if (instance == null || !dbExists) {
            instance = new DatabaseFactory(dbFile);
        }
    }

    public static DatabaseFactory instance() throws DatabaseException {

        if (instance == null) {
            throw new DatabaseException("You must call initInstance first");
        }

        return instance;
    }

    public static void kill() {
        instance = null;
    }

    public Database getDatabase(DatabaseType databaseType) throws DatabaseException {

        switch(databaseType) {
            case NODES:
                return nodesDatabase;
            case TASKS:
                return tasksDatabase;
            case CONTEXTS:
                return contextsContentsDirectoryDatabase;
        }

        throw new DatabaseException("Database type is unknown");
    }
}
