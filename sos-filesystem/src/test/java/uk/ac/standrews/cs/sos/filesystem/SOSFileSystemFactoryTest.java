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

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemFactoryTest {

    private SOS_LOG SOS_LOG = new SOS_LOG(GUIDFactory.generateRandomGUID());

    @BeforeMethod
    public void setUp() {
        // TODO - delete webdav folder content
    }

    @Test (expectedExceptions = FileSystemCreationException.class)
    public void makeFileSystemWithGUIDTest() throws FileSystemCreationException {
        IGUID guid = GUIDFactory.generateRandomGUID();
        SOSFileSystemFactory fileSystemFactory = new SOSFileSystemFactory(guid);

        fileSystemFactory.makeFileSystem();
    }

    @Test
    public void makeFileSystemTest() throws FileSystemCreationException, TIPNotFoundException, ManifestNotMadeException, ManifestNotFoundException, ManifestPersistException, FileNotFoundException, RoleNotFoundException, ServiceException {
        IGUID invariant = GUIDFactory.generateRandomGUID();
        IGUID versionGUID = GUIDFactory.generateRandomGUID();

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

        IGUID contentsGUID = GUIDFactory.generateRandomGUID();

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