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
