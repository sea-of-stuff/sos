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
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.database.DatabaseFactory;
import uk.ac.standrews.cs.sos.impl.database.DatabaseType;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.impl.services.SOSManifestsDataService;
import uk.ac.standrews.cs.sos.impl.services.SOSNodeDiscoveryService;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static uk.ac.standrews.cs.sos.constants.Internals.DB_FILE;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeRegistrationTest extends ProtocolTest {

    private SOSNodeDiscoveryService nds;
    private static IGUID localNodeGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10007;

    private static final String TEST_DATA =
            "{" +
                    "    \"guid\": \"" + localNodeGUID.toMultiHash()  + "\"," +
                    "    \"hostname\": \"localhost\"," +
                    "    \"port\": 8080," +
                    "    \"services\": {" +
                    "        \"storage\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"cms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"mds\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"nds\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"rms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"mms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"experiment\": {" +
                    "            \"exposed\": true" +
                    "        }" +
                    "    }" +
                    "}";

    private static final String TEST_DATA_FAIL =
            "{" +
                    "    \"guid\": \"" + localNodeGUID.toMultiHash()  + "\"," +
                    "    \"hostname\": \"localhost\"," +
                    "    \"port\": 8081," +
                    "    \"services\": {" +
                    "        \"storage\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"cms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"mds\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"nds\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"rms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"mms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"experiment\": {" +
                    "            \"exposed\": true" +
                    "        }" +
                    "    }" +
                    "}";

    @BeforeMethod
    public void setUp() throws ConfigurationException, IOException, SOSException, GUIDGenerationException {
        super.setUp();

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/node_registration_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        NodesDatabase nodesDatabase;
        try {
            // Make sure that the DB path is clean
            localStorage.getNodeDirectory().remove(DB_FILE);
            IFile dbFile = localStorage.createFile(localStorage.getNodeDirectory(), DB_FILE);
            DatabaseFactory.initInstance(dbFile);
            nodesDatabase = (NodesDatabase) DatabaseFactory.instance().getDatabase(DatabaseType.NODES);
        } catch (DatabaseException | BindingAbsentException e) {
            throw new SOSException(e);
        }

        LocalStorage localStorage;
        try {
            CastoreBuilder castoreBuilder = settings.getStore().getCastoreBuilder();
            IStorage stor = CastoreFactory.createStorage(castoreBuilder);
            localStorage = new LocalStorage(stor);
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        Node localNode = mock(SOSLocalNode.class);
        when(localNode.guid()).thenReturn(localNodeGUID);

        nds = new SOSNodeDiscoveryService(localNode, nodesDatabase);
        ManifestsDataService manifestsDataService = new SOSManifestsDataService(settings.getServices().getMds(), localStorage, nds);
        nds.setMDS(manifestsDataService);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/nds/register")
                                .withBody(TEST_DATA)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                );

        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/nds/register")
                                .withBody(TEST_DATA_FAIL)
                )
                .respond(
                        response()
                                .withStatusCode(500)
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {

        DatabaseFactory.kill();

        mockServer.stop();
    }


    @Test
    public void basicRegistrationTest() throws NodeRegistrationException {

        Node nodeMock = makeMockNode();
        Node registeredNode = nds.registerNode(nodeMock, true);
        assertNotNull(registeredNode);
        assertEquals(registeredNode, nodeMock);
    }

    @Test (expectedExceptions = NodeRegistrationException.class)
    public void registrationFailsTest() throws NodeRegistrationException {

        nds.registerNode(null, true);
    }

    @Test
    public void registerToNDSTest() throws NodeRegistrationException {

        Node nodeMock = new SOSNode(localNodeGUID, mockD_PublicKey, "localhost", 8080, true, true, false, false, false, false, false, false);
        Node registeredNode = nds.registerNode(nodeMock, false);
        assertNotNull(registeredNode);
        assertEquals(registeredNode, nodeMock);
    }

    /**
     * The failure is logged, but nothing is returned to the user.
     * @throws NodeRegistrationException if the node could not be registered. Test will fail
     */
    @Test
    public void registerToNDSFailsTest() throws NodeRegistrationException {

        Node nodeMock = new SOSNode(localNodeGUID, mockD_PublicKey, "localhost", 8081, true, true, false, false, false, false, false, false);
        Node registeredNode = nds.registerNode(nodeMock, false);
        assertNotNull(registeredNode);
        assertEquals(registeredNode, nodeMock);
    }

    private Node makeMockNode() {
        return new SOSNode(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), mockD_PublicKey, "localhost", 8090, true, true, true, true, true, true, true, true);
    }
}