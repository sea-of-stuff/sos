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
package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.exceptions.IgnoreException;

import java.io.*;

/**
 * Utility methods to facilitate the persistence of objects.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Persistence {

    public static void persist(Object object, IFile file) throws IOException {
        if (!file.exists()) {
            try {
                file.persist();
            } catch (PersistenceException e) {
                throw new IOException(e);
            }
        }

        try (OutputStream ostream = file.getOutputStream();
             ObjectOutputStream p = new ObjectOutputStream(ostream)) {

            p.writeObject(object);
            p.flush();
        }
    }

    public static Object load(IFile file) throws IOException, ClassNotFoundException, IgnoreException {

        // Check that file is not empty
        if (!file.exists() || file.getSize() == 0) {
            throw new IgnoreException("File is empty");
        }

        try (InputStream istream = file.getData().getInputStream();
             ObjectInputStream q = new ObjectInputStream(istream)) {

            return q.readObject();
        } catch (DataException e) {
            throw new IOException(e);
        }
    }
}
