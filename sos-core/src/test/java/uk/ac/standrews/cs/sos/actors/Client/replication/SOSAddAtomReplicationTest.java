package uk.ac.standrews.cs.sos.actors.Client.replication;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddAtomReplicationTest extends ClientReplicationTest {

    @Test
    public void replicateDataAndFetchItTest() throws Exception {

        InputStream stream = HelperTest.StringToInputStream(TEST_DATA);
        AtomBuilder builder = new AtomBuilder().setInputStream(stream);
        Atom manifest = agent.addAtom(builder);

        Thread.sleep(1000); // Let replication happen

        assertNotNull(manifest.getContentGUID());
        assertEquals(1, manifest.getLocations().size());

        // Delete atom and atom manifest
        localStorage.getManifestDirectory().remove(manifest.guid() + ".json");
        localStorage.getDataDirectory().remove(manifest.guid().toString());

        // Look at locationIndex in atomStorage
        // Get data from external source (data is never kept in memory, unlike manifests)
        InputStream inputStream = agent.getAtomContent(manifest);
        assertNotNull(inputStream);

        String retrievedData = HelperTest.InputStreamToString(inputStream);
        assertEquals(retrievedData, TEST_DATA);
    }

    @Test
    public void fetchReplicatedDataIgnoreManifestTest() throws Exception {

        InputStream stream = HelperTest.StringToInputStream(TEST_DATA);
        AtomBuilder builder = new AtomBuilder().setInputStream(stream);
        Atom manifest = agent.addAtom(builder);

        Thread.sleep(1000); // Let replication happen

        assertNotNull(manifest.getContentGUID());
        assertEquals(1, manifest.getLocations().size());

        // Delete atom ONLY
        localStorage.getDataDirectory().remove(manifest.guid().toString());

        // Manifest is ignored
        // Get data from external source (data is never kept in memory, unlike manifests)
        InputStream inputStream = agent.getAtomContent(manifest);
        assertNotNull(inputStream);

        String retrievedData = HelperTest.InputStreamToString(inputStream);
        assertEquals(retrievedData, TEST_DATA);
    }
}
