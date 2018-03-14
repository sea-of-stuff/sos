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
import uk.ac.standrews.cs.sos.impl.protocol.Task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TasksDatabase extends AbstractDatabase {

    private static final String SQL_CHECK_TASKS_TABLE_EXISTS = "SELECT name FROM sqlite_master WHERE type=\'table\' and name=\'tasks\'";
    private static final String SQL_CREATE_TASKS_TABLE = "CREATE TABLE `tasks` " +
            "(`DB_taskid`       INTEGER , " +
            "PRIMARY KEY (`DB_taskid`) )";

    public TasksDatabase(IFile dbFile) throws DatabaseException {
        super(dbFile);

        try (Connection connection = getSQLiteConnection()) {

            boolean tableExists = executeQuery(connection, SQL_CHECK_TASKS_TABLE_EXISTS);
            if (!tableExists) {
                executeUpdate(connection, SQL_CREATE_TASKS_TABLE);
            }

        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void addTask(Task task) {


        // TODO - add task description to db. The task must be serializable.
    }

    public List<Task> getTasks() {

        // return a list of tasks (must be able to deserialise them from the db)
        return new LinkedList<>();
    }

}
