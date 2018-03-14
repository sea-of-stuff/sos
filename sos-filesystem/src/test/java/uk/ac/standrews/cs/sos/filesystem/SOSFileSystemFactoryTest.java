/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module filesystem.
 *
 * filesystem is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * filesystem is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with filesystem. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.filesystem;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.model.Compound;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.services.Agent;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.FileNotFoundException;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemFactoryTest {

    private SOS_LOG SOS_LOG = new SOS_LOG(GUIDFactory.generateRandomGUID(GUID_ALGORITHM));

    @BeforeMethod
    public void setUp() {
        // TODO - delete webdav folder content
    }

    @Test (expectedExceptions = FileSystemCreationException.class)
    public void makeFileSystemWithGUIDTest() throws FileSystemCreationException {
        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        SOSFileSystemFactory fileSystemFactory = new SOSFileSystemFactory(guid);

        fileSystemFactory.makeFileSystem();
    }

    @Test
    public void makeFileSystemTest() throws FileSystemCreationException, TIPNotFoundException, ManifestNotMadeException, ManifestNotFoundException, ManifestPersistException, FileNotFoundException, RoleNotFoundException, ServiceException {
        IGUID invariant = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        IGUID versionGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);

        SOSFileSystemFactory.WriteCurrentVersion(invariant, versionGUID);

        Agent mockAgent = mockAgent(invariant, versionGUID);

        SOSFileSystemFactory fileSystemFactory = new SOSFileSystemFactory(mockAgent, invariant);
        IFileSystem fileSystem = fileSystemFactory.makeFileSystem();

        assertNotNull(fileSystem);
        assertEquals(versionGUID, fileSystem.getRootId());
    }

    private Agent mockAgent(IGUID guid, IGUID versionGUID) throws ManifestPersistException, ManifestNotMadeException, ManifestNotFoundException, TIPNotFoundException, RoleNotFoundException, ServiceException {
        Agent mockAgent = mock(Agent.class);
        Compound mockRootFolder = mock(Compound.class);
        Version mockRootVersion = mock(Version.class);

        IGUID contentsGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);

        when(mockAgent.addCompound(any(CompoundBuilder.class))).thenReturn(mockRootFolder);
        when(mockAgent.addVersion(any())).thenReturn(mockRootVersion);

        when(mockRootFolder.getContents()).thenReturn(Collections.emptySet());
        when(mockRootVersion.invariant()).thenReturn(guid);
        when(mockRootVersion.version()).thenReturn(versionGUID);
        when(mockRootVersion.content()).thenReturn(contentsGUID);

        when(mockAgent.getManifest(contentsGUID)).thenReturn(mockRootFolder);
        when(mockAgent.getManifest(versionGUID)).thenReturn(mockRootVersion);

        return mockAgent;
    }
}