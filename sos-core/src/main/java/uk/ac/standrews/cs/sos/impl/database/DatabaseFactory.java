package uk.ac.standrews.cs.sos.impl.database;

import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.interfaces.database.Database;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DatabaseFactory {

    private NodesDatabase nodesDatabase;
    private TasksDatabase tasksDatabase;


    public DatabaseFactory(String path) throws DatabaseException {

        nodesDatabase = new NodesDatabaseImpl(path);
        tasksDatabase = new TasksDatabase(path);
    }

    public Database getDatabase(DatabaseType databaseType) throws SOSException {

        switch(databaseType) {
            case NODES:
                return nodesDatabase;
            case TASKS:
                return tasksDatabase;
        }

        throw new SOSException("Database type is unknown");
    }
}
