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
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.interfaces.database.Database;

import java.sql.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
class AbstractDatabase implements Database {

    private String dbPath;

    AbstractDatabase(IFile dbFile) {
        this.dbPath = dbFile.getPathname();
    }

    /**
     * Caller must make sure that the connection is closed
     *
     * @return connection
     * @throws DatabaseConnectionException if connection could not be established
     */
    Connection getSQLiteConnection() throws DatabaseConnectionException {

        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    boolean executeQuery(Connection connection, String query) throws SQLException {

        boolean retval;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet  = preparedStatement.executeQuery()) {

            retval = resultSet.next();
        }

        return retval;
    }

    void executeUpdate(Connection connection, String query) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.executeUpdate();
        }
    }
}
