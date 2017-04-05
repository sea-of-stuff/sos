package uk.ac.standrews.cs.sos.impl.actors.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.DDS;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.ManifestReplication;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestReplicationTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10002;

    private static final String TEST_MANIFEST =
            "{\n" +
            "  \"Type\" : \"Version\",\n" +
            "  \"ContentGUID\" : \"27c5a764bb09f0d737fbce4daaedb4f8b8d4ade0\",\n" +
            "  \"Invariant\" : \"5f6953558817f20a99194fde4d8d5365cef30225\",\n" +
            "  \"GUID\" : \"2dcfc250dda1df3e50fac249af6df531d486e7e3\",\n" +
            "  \"Previous\" : [ \"dcb0cece956ead212dcd99458408534d25a94da9\" ],\n" +
            "  \"Signature\" : \"MCwCFBEWwqB+/f7s5iCzdxFc/N4FrIQtAhRB07czCQZ+G6dnlM6XrXTb1jqXeA==\"\n" +
            "}";

    private static final String TEST_BAD_MANIFEST = "BAD Manifest";

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException {

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/dds/manifest")
                                .withBody(TEST_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/dds/manifest")
                                .withBody(TEST_BAD_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(400)
                );

        SOSURLProtocol.getInstance().register(null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicManifestReplicationTest() throws InterruptedException, SOSProtocolException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_MANIFEST);

        Node node = mock(Node.class);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        DDS ddsMock = mock(DDS.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodes.iterator(), 1, ddsMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).isDDS();
        verify(node, times(1)).getHostAddress();

        verify(ddsMock, times(1)).addManifestDDSMapping(anyObject(), anyObject());
    }

    @Test
    public void cannotReplicateManifestToNoDDSNodeReplicationTest() throws InterruptedException, SOSProtocolException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_MANIFEST);

        Node node = mock(Node.class);
        when(node.isDDS()).thenReturn(false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        DDS ddsMock = mock(DDS.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodes.iterator(), 1, ddsMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).isDDS();
        verify(node, times(0)).getHostAddress();

        verify(ddsMock, times(0)).addManifestDDSMapping(anyObject(), anyObject());
    }

    @Test (expectedExceptions = SOSProtocolException.class)
    public void basicManifestReplicationFailsTest() throws InterruptedException, SOSProtocolException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_MANIFEST);

        Node node = mock(Node.class);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodes.iterator(), 1, null);
    }

    @Test
    public void badManifestReplicationTest() throws InterruptedException, SOSProtocolException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_BAD_MANIFEST);

        Node node = mock(Node.class);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        DDS ddsMock = mock(DDS.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodes.iterator(), 1, ddsMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).isDDS();
        verify(node, times(1)).getHostAddress();

        verify(ddsMock, times(0)).addManifestDDSMapping(anyObject(), anyObject());
    }
}
