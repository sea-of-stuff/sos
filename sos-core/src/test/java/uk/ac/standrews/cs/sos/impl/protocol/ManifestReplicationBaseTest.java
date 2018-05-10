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
package uk.ac.standrews.cs.sos.impl.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestReplicationBaseTest extends SetUpTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10002;

    private static final String GUID_VERSION = "SHA256_16_aaaaa025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TEST_VERSION_MANIFEST = "" +
            "{" +
            "  \"type\":\"Version\"," +
            "  \"invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "  \"guid\":\""+ GUID_VERSION+"\"," +
            "  \"signature\":\"AAAB\"," +
            "  \"metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "  \"previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
            "  \"content\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
            "}";

    private static final String GUID_COMPOUND = "SHA256_16_964dab35b9136a610687d31b56fd346bdda027be0a66e6761e0fd1238262cd9f";
    private static final String TEST_COMPOUND_MANIFEST = "" +
            "{" +
            "  \"type\":\"Compound\"," +
            "  \"guid\":\"" + GUID_COMPOUND + "\"," +
            "  \"signature\":\"AAAB\"," +
            "  \"signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
            "  \"compound_type\":\"DATA\"," +
            "  \"contents\":" +
            "  [{" +
            "    \"label\":\"cat\"," +
            "    \"guid\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
            "  }]" +
            "}";


    private static final String GUID_ATOM = "SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7"; // hash of "data"
    private static final String NODE_ID = "SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TEST_ATOM_MANIFEST = "" +
            "{\n" +
            "    \"type\" : \"Atom\",\n" +
            "    \"guid\" : \"SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7\",\n" +
            "    \"locations\" : \n" +
            "    [\n" +
            "          {\n" +
            "            \"type\" : \"persistent\",\n" +
            "            \"location\" : \"sos://" + NODE_ID + "/" + GUID_ATOM + "\"\n" +
            "        } \n" +
            "    ]\n" +
            "}";

    private static final String TEST_FAT_CONTEXT_MANIFEST = "" +
            "{\n" +
            "  \"context\": {\n" +
            "    \"name\": \"All\",\n" +
            "    \"domain\": {\n" +
            "      \"type\": \"LOCAL\",\n" +
            "      \"nodes\": []\n" +
            "    },\n" +
            "    \"codomain\": {\n" +
            "      \"type\": \"SPECIFIED\",\n" +
            "      \"nodes\": [\"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"]\n" +
            "    }\n" +
            "  },\n" +
            "  \"predicate\": {\n" +
            "    \"type\": \"Predicate\",\n" +
            "    \"predicate\": \"true;\"\n" +
            "  },\n" +
            "  \"max_age\": 0,\n" +
            "  \"policies\": [{\n" +
            "    \"type\": \"Policy\",\n" +
            "    \"apply\": \"\",\n" +
            "    \"satisfied\": \"return true;\"\n" +
            "  }]\n" +
            "}";

    private static final String TEST_THIN_CONTEXT_MANIFEST = "" +
            "{\n" +
            "  \"type\" : \"Context\",\n" +
            "  \"guid\" : \"SHA256_16_e9561e61c65158f11fcf3d553ba0045a882f340ba6461480c2aab7feef4c672e\",\n" +
            "  \"name\" : \"All\",\n" +
            "  \"invariant\" : \"SHA256_16_76ad2bb7f12f9cc8ecf515931b374f3533527a206ebd909d015a5744c812e57e\",\n" +
            "  \"content\" : \"SHA256_16_95a44980ed66c2f36eb647520aaaf10cbf891ec1ce448ef0c3ce387634be118f\",\n" +
            "  \"domain\" : {\n" +
            "    \"type\" : \"LOCAL\",\n" +
            "    \"nodes\" : [ ]\n" +
            "  },\n" +
            "  \"codomain\" : {\n" +
            "    \"type\" : \"SPECIFIED\",\n" +
            "    \"nodes\" : [ \"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\" ]\n" +
            "  },\n" +
            "  \"predicate\" : \"SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e\",\n" +
            "  \"max_age\" : 0,\n" +
            "  \"policies\" : [ \"SHA256_16_0c094bb01ae9803b22b2c9dd4b350b3456c168eca5173002cdd01c7cd1f09905\" ]\n" +
            "}";

    private static final String TEST_PREDICATE_MANIFEST = "" +
            "{\n" +
            "  \"type\" : \"Predicate\",\n" +
            "  \"guid\" : \"SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e\",\n" +
            "  \"predicate\" : \"true;\"\n" +
            "}";

    private static final String TEST_POLICY_MANIFEST = "" +
            "{\n" +
            "  \"type\" : \"Policy\",\n" +
            "  \"guid\" : \"SHA256_16_0c094bb01ae9803b22b2c9dd4b350b3456c168eca5173002cdd01c7cd1f09905\",\n" +
            "  \"apply\" : \"\",\n" +
            "  \"satisfied\" : \"return true;\",\n" +
            "  \"fields\" : [ ]\n" +
            "}";


    private static final String TEST_BAD_MANIFEST = "BAD Manifest";

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/manifest_replication_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        new SOS_LOG(GUIDFactory.generateRandomGUID(GUID_ALGORITHM));

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();

        // POST VERSION
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/mds/manifest")
                                .withBody(TEST_VERSION_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        // POST COMPOUND
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/mds/manifest")
                                .withBody(TEST_COMPOUND_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        // POST ATOM
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/mds/manifest")
                                .withBody(TEST_ATOM_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        // POST CONTEXT
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/cms/context")
                                .withBody(TEST_FAT_CONTEXT_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        // POST BAD MANIFEST
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/mds/manifest")
                                .withBody(TEST_BAD_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(400)
                );
    }

    @AfterMethod
    public void tearDown() {
        RequestsManager.getInstance().shutdown();

        mockServer.stop();

        // Let the mock server stop properly
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void basicVersionManifestReplicationTest(boolean isSequential) throws SOSProtocolException, NodeNotFoundException {
        System.out.println("---> Sequential: " + isSequential);

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_VERSION_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.VERSION);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isMDS()).thenReturn(true);
        when(node.getIP()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, isSequential, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();
        verify(manifestsDataServiceMock, times(1)).addManifestNodeMapping(anyObject(), anyObject());
    }

    void basicCompoundManifestReplicationTest(boolean isSequential) throws SOSProtocolException, NodeNotFoundException {
        System.out.println("---> Sequential: " + isSequential);

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_COMPOUND_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.COMPOUND);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isMDS()).thenReturn(true);
        when(node.getIP()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, isSequential, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();
        verify(manifestsDataServiceMock, times(1)).addManifestNodeMapping(anyObject(), anyObject());
    }

    void basicAtomManifestReplicationTest(boolean isSequential) throws SOSProtocolException, NodeNotFoundException, GUIDGenerationException {
        System.out.println("---> Sequential: " + isSequential);

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_ATOM_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.ATOM);

        IGUID nodeGUID = GUIDFactory.recreateGUID(NODE_ID);
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isMDS()).thenReturn(true);
        when(node.getIP()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, isSequential, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();
        verify(manifestsDataServiceMock, times(1)).addManifestNodeMapping(anyObject(), anyObject());
    }

    void basicFATContextManifestReplicationTest(boolean isSequential) throws SOSProtocolException, NodeNotFoundException, GUIDGenerationException, ManifestNotFoundException, IOException {
        System.out.println("---> Sequential: " + isSequential);

        Context mockManifest = mock(Context.class);
        when(mockManifest.toString()).thenReturn(TEST_THIN_CONTEXT_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.CONTEXT);
        when(mockManifest.predicate()).thenReturn(GUIDFactory.recreateGUID("SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e"));
        IGUID policyRef = GUIDFactory.recreateGUID("SHA256_16_0c094bb01ae9803b22b2c9dd4b350b3456c168eca5173002cdd01c7cd1f09905");
        Set<IGUID> policieRefs = new LinkedHashSet<>();
        policieRefs.add(policyRef);
        when(mockManifest.policies()).thenReturn(policieRefs);
        when(mockManifest.toFATString(any(), any())).thenReturn(TEST_FAT_CONTEXT_MANIFEST);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isMDS()).thenReturn(false);
        when(node.isCMS()).thenReturn(true);
        when(node.getIP()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);
        when(manifestsDataServiceMock.getManifest(
                GUIDFactory.recreateGUID("SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e")))
                .thenReturn(FileUtils.ManifestFromJson(TEST_PREDICATE_MANIFEST));
        when(manifestsDataServiceMock.getManifest(
                GUIDFactory.recreateGUID("SHA256_16_0c094bb01ae9803b22b2c9dd4b350b3456c168eca5173002cdd01c7cd1f09905")))
                .thenReturn(FileUtils.ManifestFromJson(TEST_POLICY_MANIFEST));

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, isSequential, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();
        verify(manifestsDataServiceMock, times(1)).addManifestNodeMapping(anyObject(), anyObject());
    }

    // Cannot replicate VERSION manifest to noMDS node
    void cannotReplicateManifestToNoMDSNodeReplicationTest(boolean isSequential) throws SOSProtocolException, NodeNotFoundException {
        System.out.println("---> Sequential: " + isSequential);

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_VERSION_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.VERSION);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isMDS()).thenReturn(false);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, isSequential, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(0)).getHostAddress();
        verify(manifestsDataServiceMock, times(0)).addManifestNodeMapping(anyObject(), anyObject());
    }

    void basicManifestReplicationFailsTest(boolean isSequential) throws SOSProtocolException {
        System.out.println("---> Sequential: " + isSequential);

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_VERSION_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.VERSION);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isMDS()).thenReturn(true);
        when(node.getIP()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, isSequential, null, null);
    }

    void badManifestReplicationTest(boolean isSequential) throws SOSProtocolException, NodeNotFoundException {
        System.out.println("---> Sequential: " + isSequential);

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_BAD_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.ATOM); // It can actually be whatever (except context)

        IGUID nodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isMDS()).thenReturn(true);
        when(node.getIP()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, isSequential, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();
        verify(manifestsDataServiceMock, times(0)).addManifestNodeMapping(anyObject(), anyObject());
    }
}
