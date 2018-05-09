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
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.FetchManifest;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
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
public class FetchManifestTest extends ProtocolTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10005;

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

    @BeforeMethod
    public void setUp() throws ConfigurationException, GUIDGenerationException, SOSException, IOException {
        super.setUp();

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/fetch_manifest_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/sos/mds/manifest/guid/" + GUID_VERSION)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(TEST_VERSION_MANIFEST)
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicManifestFetchTest() throws IOException, GUIDGenerationException {

        Node node = new SOSNode(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), mockD_PublicKey,
                "localhost", MOCK_SERVER_PORT,
                false, false, true, false, false, false, false, false);

        IGUID testGUID = GUIDFactory.recreateGUID(GUID_VERSION);

        FetchManifest fetchManifest = new FetchManifest(node, testGUID);
        TasksQueue.instance().performSyncTask(fetchManifest);

        Manifest manifest = fetchManifest.getManifest();
        assertNotNull(manifest);
        assertEquals(manifest.getType(), ManifestType.VERSION);
        assertEquals(manifest.guid(), testGUID);
    }

}
