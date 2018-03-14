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
package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.IgnoreException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.utils.Persistence;

import java.io.IOException;
import java.util.Set;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsDataServiceIndexTest {

    @Test
    public void basicTest() {
        IGUID manifestGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        IGUID nodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);

        ManifestsLocationsIndex index = new ManifestsLocationsIndex();
        index.addEntry(manifestGUID, nodeGUID);
        Set<IGUID> nodesGUIDs = index.getNodeRefs(manifestGUID);
        assertNotNull(nodesGUIDs);
        assertNotEquals(nodesGUIDs.size(), 0);
        assertEquals(nodesGUIDs.size(), 1);
        assertTrue(nodesGUIDs.contains(nodeGUID));
    }

    @Test
    public void nullTest() {
        IGUID manifestGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);

        ManifestsLocationsIndex index = new ManifestsLocationsIndex();
        Set<IGUID> nodesGUIDs = index.getNodeRefs(manifestGUID);
        assertEquals(nodesGUIDs.size(), 0);
    }

    @Test
    public void evictTest() throws ManifestNotFoundException {
        IGUID manifestGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        IGUID nodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);

        ManifestsLocationsIndex index = new ManifestsLocationsIndex();
        index.addEntry(manifestGUID, nodeGUID);
        Set<IGUID> nodesGUIDs = index.getNodeRefs(manifestGUID);
        assertEquals(nodesGUIDs.size(), 1);

        index.evictEntry(manifestGUID, nodeGUID);
        Set<IGUID> nodesGUIDsSecondTime = index.getNodeRefs(manifestGUID);
        assertEquals(nodesGUIDsSecondTime.size(), 0);
    }

    @Test
    public void persistIndexTest() throws IOException, ClassNotFoundException, DataStorageException, StorageException, IgnoreException {

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);

        LocalStorage localStorage = new LocalStorage(stor);
        IDirectory cachesDir = localStorage.getNodeDirectory();

        IGUID manifestGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        IGUID nodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);

        ManifestsLocationsIndex manifestsLocationsIndex = new ManifestsLocationsIndex();
        manifestsLocationsIndex.addEntry(manifestGUID, nodeGUID);

        IFile file = localStorage.createFile(cachesDir, "mds.index");
        Persistence.persist(manifestsLocationsIndex, file);

        ManifestsLocationsIndex persistedIndex = (ManifestsLocationsIndex) Persistence.load(file);
        Set<IGUID> nodesGUIDs = persistedIndex.getNodeRefs(manifestGUID);
        assertNotNull(nodesGUIDs);
        assertNotEquals(nodesGUIDs.size(), 0);
        assertEquals(nodesGUIDs.size(), 1);
        assertTrue(nodesGUIDs.contains(nodeGUID));

    }

    @Test
    public void persistEmptyIndexTest() throws IOException, ClassNotFoundException, DataStorageException, StorageException, IgnoreException {

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);

        LocalStorage localStorage = new LocalStorage(stor);
        IDirectory cachesDir = localStorage.getNodeDirectory();

        ManifestsLocationsIndex manifestsLocationsIndex = new ManifestsLocationsIndex();

        IFile file = localStorage.createFile(cachesDir, "mds.index");
        Persistence.persist(manifestsLocationsIndex, file);

        ManifestsLocationsIndex persistedIndex = (ManifestsLocationsIndex) Persistence.load(file);
        IGUID manifestGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Set<IGUID> nodesGUIDs = persistedIndex.getNodeRefs(manifestGUID);
        assertEquals(nodesGUIDs.size(), 0);
    }
}