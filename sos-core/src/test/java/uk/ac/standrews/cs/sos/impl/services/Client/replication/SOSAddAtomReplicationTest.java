package uk.ac.standrews.cs.sos.impl.services.Client.replication;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddAtomReplicationTest extends ClientReplicationTest {

    @Test
    public void replicateDataAndFetchItTest() throws Exception {

        Node node = new SOSNode(GUIDFactory.recreateGUID(NODE_ID), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false, false);
        nds.registerNode(node, true);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(node.guid());
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        InputStream stream = HelperTest.StringToInputStream(TEST_DATA);
        AtomBuilder builder = new AtomBuilder()
                .setData(new InputStreamData(stream))
                .setReplicationFactor(1)
                .setReplicationNodes(nodesCollection);
        Atom manifest = agent.addAtom(builder);

        Thread.sleep(1000); // Let replication happen

        assertNotNull(manifest.guid());
        assertEquals(1, manifest.getLocations().size());

        // Delete atom and atom manifest
        localStorage.getManifestsDirectory().remove(manifest.guid().toMultiHash() + ".json");
        localStorage.getDataDirectory().remove(manifest.guid().toMultiHash());

        // Look at locationIndex in atomStorage
        // Get data from external source (data is never kept in memory, unlike manifests)
        Data data = agent.getAtomContent(manifest);
        assertNotNull(data);

        String retrievedData = data.toString();
        assertEquals(retrievedData, TEST_DATA);

        data.close();
    }

    @Test
    public void fetchReplicatedDataIgnoreManifestTest() throws Exception {

        Node node = new SOSNode(GUIDFactory.recreateGUID(NODE_ID), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false, false);
        nds.registerNode(node, true);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(node.guid());
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        InputStream stream = HelperTest.StringToInputStream(TEST_DATA);
        AtomBuilder builder = new AtomBuilder()
                .setData(new InputStreamData(stream))
                .setReplicationFactor(1)
                .setReplicationNodes(nodesCollection);
        Atom manifest = agent.addAtom(builder);

        Thread.sleep(3000); // Let replication happen asynchronously

        assertNotNull(manifest.guid());
        assertEquals(1, manifest.getLocations().size());

        // TODO - make sure that atom can be retrieved from remote?

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
