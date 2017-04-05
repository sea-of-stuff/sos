package uk.ac.standrews.cs.sos.filesystem.impl;


import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.store.impl.localfilebased.StringData;
import uk.ac.standrews.cs.sos.actors.Agent;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Collections;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemTest {

    private SOS_LOG SOS_LOG = new SOS_LOG(GUIDFactory.generateRandomGUID());

    @Test
    public void constructorTest() throws Exception {
        IGUID invariant = GUIDFactory.generateRandomGUID();
        IGUID versionGUID = GUIDFactory.generateRandomGUID();
        Agent mockAgent = mockAgent(invariant, versionGUID);

        SOSFileSystem sosFS = new SOSFileSystem(mockAgent, (Version) mockAgent.getManifest(versionGUID));
        assertNotNull(sosFS);
    }

    @Test
    public void newFileTest() throws Exception {
        IGUID invariant = GUIDFactory.generateRandomGUID();
        IGUID versionGUID = GUIDFactory.generateRandomGUID();
        Agent mockAgent = mockAgent(invariant, versionGUID);

        SOSFileSystem sosFS = new SOSFileSystem(mockAgent, (Version) mockAgent.getManifest(versionGUID));
        IFile file = sosFS.createNewFile(sosFS.getRootDirectory(), "TEST", "n/a", new StringData("TEST DATA"));

        assertNotNull(file);
        assertNotNull(file.getGUID());
        assertNull(file.getName());
    }

    private Agent mockAgent(IGUID guid, IGUID versionGUID) throws Exception {
        Agent mockAgent = mock(Agent.class);
        Compound mockRootFolder = mock(Compound.class);
        Version mockRootVersion = mock(Version.class);

        IGUID contentsGUID = GUIDFactory.recreateGUID("61e6e9e4a918693e3a14acf042af0dab28ef6f77");
        IGUID rootGUID = GUIDFactory.recreateGUID("aaa6e9e4a918693e3a14acf042af0dab28ef6f77");

        when(mockAgent.addCompound(any(), anySetOf(Content.class))).thenReturn(mockRootFolder);
        when(mockAgent.addVersion(any())).thenReturn(mockRootVersion);

        when(mockRootFolder.getContents()).thenReturn(Collections.emptySet());
        when(mockRootFolder.guid()).thenReturn(rootGUID);

        when(mockRootVersion.getInvariantGUID()).thenReturn(guid);
        when(mockRootVersion.getVersionGUID()).thenReturn(versionGUID);
        when(mockRootVersion.getContentGUID()).thenReturn(contentsGUID);
        when(mockRootVersion.getPreviousVersions()).thenReturn(Collections.emptySet());

        SOSFileSystemFactory.WriteCurrentVersion(guid, versionGUID);

        when(mockAgent.getManifest(contentsGUID)).thenReturn(mockRootFolder);
        when(mockAgent.getManifest(versionGUID)).thenReturn(mockRootVersion);


        Atom mockAtom = mock(Atom.class);
        when(mockAgent.addAtom(anyObject())).thenReturn(mockAtom);

        Metadata mockMeta = mock(Metadata.class);
        when(mockAgent.addMetadata(mockAtom)).thenReturn(mockMeta);

        return mockAgent;
    }

}