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
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.FetchManifest;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.File;
import java.io.IOException;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchMetadataTest extends ProtocolTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10005;

    private static final String GUID_METADATA = "SHA256_16_aaaaa025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TEST_METADATA =
            "{\n" +
                    "    \"guid\": \"SHA256_16_aaaaa025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\",\n" +
                    "    \"type\":\"Metadata\"," +
                    "    \"properties\": [\n" +
                    "        {\n" +
                    "            \"key\": \"X-Parsed-By\",\n" +
                    "            \"type\": \"string\",\n" +
                    "            \"value\": \"org.apache.tika.parser.DefaultParser\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"key\": \"Size\",\n" +
                    "            \"type\": \"long\",\n" +
                    "            \"value\": 26\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"key\": \"Timestamp\",\n" +
                    "            \"type\": \"long\",\n" +
                    "            \"value\": 1484736105\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"key\": \"Content-Type\",\n" +
                    "            \"type\": \"string\",\n" +
                    "            \"value\": \"text/plain; charset=ISO-8859-1\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";

    @BeforeMethod
    public void setUp() throws GUIDGenerationException, ConfigurationException, SOSException, IOException {
        super.setUp();

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/fetch_metadata_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        IGUID testGUID = GUIDFactory.recreateGUID(GUID_METADATA);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/sos/mms/metadata/guid/" + testGUID.toMultiHash())
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(TEST_METADATA)
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test(timeOut = 10000)
    public void basicMetadataFetchTest() throws IOException, GUIDGenerationException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), mockD_PublicKey,
                "localhost", MOCK_SERVER_PORT,
                false, false, false, true, true, false, false, false);

        IGUID testGUID = GUIDFactory.recreateGUID(GUID_METADATA);

        FetchManifest fetchMetadata = new FetchManifest(node, testGUID);
        TasksQueue.instance().performSyncTask(fetchMetadata);

        Metadata metadata = (Metadata) fetchMetadata.getManifest();
        assertNotNull(metadata);
        assertEquals(metadata.guid(), testGUID);
    }
}
