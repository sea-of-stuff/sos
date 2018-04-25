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

import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HelperTest {

    public static InputStream StringToInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String InputStreamToString(InputStream string) throws IOException {
        return IOUtils.toString(string, StandardCharsets.UTF_8);
    }

    private static String localSOSDataPath(LocalStorage localStorage, SOSLocation location) throws DataStorageException {

        return localStorage.getAtomsDirectory().toString() + location.getEntityID().toMultiHash();
    }

    public static Location createDummyDataFile(LocalStorage storage) throws URISyntaxException, StorageException, DataStorageException {
        return createDummyDataFile(storage, "testData.txt");
    }

    public static Location createDummyDataFile(LocalStorage storage, String filename)
            throws URISyntaxException, StorageException, DataStorageException {
        IDirectory testDir = storage.getAtomsDirectory();
        return createDummyDataFile(storage, testDir, filename);
    }

    private static Location createDummyDataFile(LocalStorage storage, IDirectory sosParent, String filename) throws URISyntaxException, StorageException, DataStorageException {

        Data data = new StringData("The first line\nThe second line");
        IFile sosFile = storage.createFile(sosParent, filename, data);
        sosFile.persist();

        return new URILocation("file://" + sosFile.getPathname());
    }

    public static void appendToFile(LocalStorage localStorage, SOSLocation location, String text) throws URISyntaxException, IOException, DataStorageException {

        String path = HelperTest.localSOSDataPath(localStorage, location);
        java.io.File file = new java.io.File(path);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true);
             PrintWriter writer = new PrintWriter(fileOutputStream)) {

            writer.append(text);
        }
    }

}
