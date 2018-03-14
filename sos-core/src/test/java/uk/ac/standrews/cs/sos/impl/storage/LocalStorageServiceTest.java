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
package uk.ac.standrews.cs.sos.impl.storage;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalStorageServiceTest extends SetUpTest {

    @Test
    public void defaultDirectoriesExistTest() throws DataStorageException {
        assertNotNull(localStorage.getDataDirectory());
        assertNotNull(localStorage.getManifestsDirectory());
    }

    @Test
    public void createFileTest() throws DataStorageException, BindingAbsentException,
            PersistenceException, DataException {

        localStorage.createFile(localStorage.getManifestsDirectory(),
                "test.txt").persist();

        IFile file = (IFile) localStorage.getManifestsDirectory().get("test.txt");
        assertNotNull(file);

        assertEquals(file.getData().getSize(), 0);
    }

    @Test
    public void createFileWithDataTest() throws DataStorageException, BindingAbsentException,
            PersistenceException, DataException {

        localStorage.createFile(localStorage.getManifestsDirectory(),
                "test.txt", new StringData("test-data")).persist();

        IFile file = (IFile) localStorage.getManifestsDirectory().get("test.txt");
        assertNotNull(file);

        assertEquals(file.getData().getSize(), 9);
    }

}
