package uk.ac.standrews.cs.sos.impl.database;

import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.interfaces.database.Database;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.utils.FileUtils;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DatabaseFactory {

    private NodesDatabase nodesDatabase;
    private TasksDatabase tasksDatabase;
    private ContextsContentsDirectoryDatabase contextsContentsDirectoryDatabase;

    private DatabaseFactory(String path) throws DatabaseException {

        nodesDatabase = new NodesDatabaseImpl(path);
        tasksDatabase = new TasksDatabase(path);
        contextsContentsDirectoryDatabase = new ContextsContentsDirectoryDatabase(path);
    }

    private static DatabaseFactory instance;

    public static void initInstance(String path) throws DatabaseException {
        FileUtils.MakePath(path);

        boolean dbExists = new File(path).exists();
        if (instance == null || !dbExists) {
            instance = new DatabaseFactory(path);
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
