package uk.ac.standrews.cs.sos.impl.services.Client.replication;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
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
        AtomBuilder builder = new AtomBuilder().setData(new InputStreamData(stream));
        Atom manifest = agent.addAtom(builder);

        Thread.sleep(1000); // Let replication happen

        assertNotNull(manifest.guid());
        assertEquals(1, manifest.getLocations().size());

        // Delete atom and atom manifest
        localStorage.getManifestsDirectory().remove(manifest.guid().toMultiHash() + ".json");
        localStorage.getDataDirectory().remove(manifest.guid().toMultiHash());

        // Look at locationIndex in atomStorage
        // Get data from external source (data is never kept in memory, unlike manifests)
        Data data  = agent.getAtomContent(manifest);
        assertNotNull(data);

        String retrievedData = data.toString();
        assertEquals(retrievedData, TEST_DATA);

        data.close();
    }

    @Test
    public void fetchReplicatedDataIgnoreManifestTest() throws Exception {

        InputStream stream = HelperTest.StringToInputStream(TEST_DATA);
        AtomBuilder builder = new AtomBuilder().setData(new InputStreamData(stream));
        Atom manifest = agent.addAtom(builder);

        Thread.sleep(1000); // Let replication happen

        assertNotNull(manifest.guid());
        assertEquals(1, manifest.getLocations().size());

        // Delete atom ONLY
        localStorage.getDataDirectory().remove(manifest.guid().toMultiHash());

        // Manifest is ignored
        // Get data from external source (data is never kept in memory, unlike manifests)
        Data data = agent.getAtomContent(manifest);
        assertNotNull(data);

        String retrievedData = data.toString();
        assertEquals(retrievedData, TEST_DATA);

        data.close();
    }
}
