package uk.ac.standrews.cs.sos.filesystem.impl;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.fs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.store.impl.localfilebased.StringData;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemTest {

    private SOS_LOG SOS_LOG = new SOS_LOG(GUIDFactory.generateRandomGUID());


    @Test
    public void constructorTest() throws HEADNotFoundException, ManifestNotMadeException, ManifestNotFoundException, ManifestPersistException, FileSystemCreationException {
        IGUID invariant = GUIDFactory.generateRandomGUID();
        IGUID versionGUID = GUIDFactory.generateRandomGUID();
        Agent mockAgent = mockAgent(invariant, versionGUID);

        SOSFileSystem sosFS = new SOSFileSystem(mockAgent, (Asset) mockAgent.getManifest(versionGUID));
        assertNotNull(sosFS);
    }

    @Test
    public void newFileTest() throws HEADNotFoundException, ManifestNotMadeException, ManifestNotFoundException, ManifestPersistException, FileSystemCreationException, BindingPresentException, PersistenceException {
        IGUID invariant = GUIDFactory.generateRandomGUID();
        IGUID versionGUID = GUIDFactory.generateRandomGUID();
        Agent mockAgent = mockAgent(invariant, versionGUID);

        SOSFileSystem sosFS = new SOSFileSystem(mockAgent, (Asset) mockAgent.getManifest(versionGUID));
        IFile file = sosFS.createNewFile(null, "TEST", "n/a", new StringData("TEST DATA"));
    }

    private Agent mockAgent(IGUID guid, IGUID versionGUID) throws ManifestPersistException, ManifestNotMadeException, ManifestNotFoundException, HEADNotFoundException {
        Agent mockAgent = mock(Agent.class);
        Compound mockRootFolder = mock(Compound.class);
        Asset mockRootAsset = mock(Asset.class);

        IGUID contentsGUID = GUIDFactory.generateRandomGUID();

        when(mockAgent.addCompound(any(), anySetOf(Content.class))).thenReturn(mockRootFolder);
        when(mockAgent.addAsset(any())).thenReturn(mockRootAsset);

        when(mockRootFolder.getContents()).thenReturn(Collections.emptySet());
        when(mockRootAsset.getInvariantGUID()).thenReturn(guid);
        when(mockRootAsset.getVersionGUID()).thenReturn(versionGUID);
        when(mockRootAsset.getContentGUID()).thenReturn(contentsGUID);

        when(mockAgent.getManifest(contentsGUID)).thenReturn(mockRootFolder);
        when(mockAgent.getManifest(versionGUID)).thenReturn(mockRootAsset);

        return mockAgent;
    }

}