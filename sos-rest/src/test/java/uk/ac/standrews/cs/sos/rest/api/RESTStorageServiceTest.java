package uk.ac.standrews.cs.sos.rest.api;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.datamodel.AtomManifest;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPStatus;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTStorageServiceTest extends CommonRESTTest {

    private static final String TEST_NODE_INFO =
            "{\n" +
                    "  \"type\": \"Atom\",\n" +
                    "  \"GUID\": \"SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"type\": \"persistent\"\n" +
                    /*"      \"location\": \"sos://SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4/SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7\"\n" +*/
                    "    }\n" +
                    "  ]\n" +
                    "}";

    private static final String TEST_EMPTY_ATOM_MANIFEST =
            "{\n" +
                    "  \"type\": \"Atom\",\n" +
                    "  \"GUID\": \"SHA256_16_e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"type\": \"persistent\"\n" +
                    /*"      \"location\": \"sos://SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4/SHA256_16_e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\"\n" +*/
                    "    }\n" +
                    "  ]\n" +
                    "}";

    private static final String BASIC_REQUEST = "" +
            "{\n" +
            "  \"guid\" : \"{GUID}\",\n" +
            "  \"data\" : \"{DATA}\"\n" +
            "}";

    private static final String REQUEST_WITH_REPLICA_INFO = "" +
            "{\n" +
            "  \"metadata\" : {\n" +
            "    \"replicationFactor\" : \"2\",\n" +
            "    \"replicationNodes\" : {\n" +
            "      \"type\" : \"SPECIFIED\",\n" +
            "      \"refs\" : [\"SHA256_16_000abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\", \"SHA256_16_111abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\"]\n" +
            "    }\n" +
            "  },\n" +
            "  \"guid\" : \"SHA256_16_d12abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\",\n" +
            "  \"data\" : \"{DATA}\"\n" +
            "}";

    private static final String REQUEST_WITH_NEGATIVE_REPLICA_INFO = "" +
            "{\n" +
            "  \"metadata\" : {\n" +
            "    \"replicationFactor\" : -1,\n" +
            "    \"replicationNodes\" : {\n" +
            "      \"type\" : \"SPECIFIED\",\n" +
            "      \"refs\" : [\"SHA256_16_000abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\", \"SHA256_16_111abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\"]\n" +
            "    }\n" +
            "  },\n" +
            "  \"guid\" : \"SHA256_16_d12abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\",\n" +
            "  \"data\" : \"{DATA}\"\n" +
            "}";

    private static final String REQUEST_WITH_EXCESSIVE_REPLICA_INFO = "" +
            "{\n" +
            "  \"metadata\" : {\n" +
            "    \"replicationFactor\" : 100,\n" +
            "    \"replicationNodes\" : {\n" +
            "      \"type\" : \"SPECIFIED\",\n" +
            "      \"refs\" : [\"SHA256_16_000abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\", \"SHA256_16_111abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\"]\n" +
            "    }\n" +
            "  },\n" +
            "  \"guid\" : \"SHA256_16_d12abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\",\n" +
            "  \"data\" : \"{DATA}\"\n" +
            "}";

    private static final String REQUEST_WITH_ZERO_REPLICA_INFO = "" +
            "{\n" +
            "  \"metadata\" : {\n" +
            "    \"replicationFactor\" : 0,\n" +
            "    \"replicationNodes\" : {\n" +
            "      \"type\" : \"SPECIFIED\",\n" +
            "      \"refs\" : [\"SHA256_16_000abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\", \"SHA256_16_111abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\"]\n" +
            "    }\n" +
            "  },\n" +
            "  \"guid\" : \"SHA256_16_d12abf2146cd61f01f96b3811e94b9dbdebd89325a24a933c50abbe664589886\",\n" +
            "  \"data\" : \"{DATA}\"\n" +
            "}";

    @Test
    public void testStoreInputStream() throws Exception {

        Response response = target("/sos/storage/stream")
                .request()
                .post(Entity.json(
                        BASIC_REQUEST.replace("{DATA}", IO.toBase64("data"))
                                .replace("{GUID}", "SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7")
                ));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), false);

        Atom atom = JSONHelper.JsonObjMapper().readValue(response.readEntity(String.class), Atom.class);
        assertNotNull(atom);
        assertEquals(atom.guid().toMultiHash(), "SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7");
        assertFalse(atom.getLocations().isEmpty());

        response.close();
    }

    @Test
    public void testStoreEmptyInputStream() throws Exception {

        Response response = target("/sos/storage/stream")
                .request()
                .post(Entity.json(
                        BASIC_REQUEST.replace("{DATA}", "")
                                .replace("{GUID}", "SHA256_16_e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
                ));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        JSONAssert.assertEquals(TEST_EMPTY_ATOM_MANIFEST, response.readEntity(String.class), false);

        Atom atom = JSONHelper.JsonObjMapper().readValue(response.readEntity(String.class), Atom.class);
        assertNotNull(atom);
        assertEquals(atom.guid().toMultiHash(), "SHA256_16_e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        assertFalse(atom.getLocations().isEmpty());

        response.close();
    }

    @Test (expectedExceptions = NullPointerException.class)
    public void testStoreNullInputStream() throws Exception {

        target("/sos/storage/stream")
                .request()
                .post(Entity.json(null));
    }

    @Test
    public void replicasStream() throws Exception {

        Response response = target("/sos/storage/stream")
                .request()
                .post(Entity.json(REQUEST_WITH_REPLICA_INFO.replace("{DATA}", IO.toBase64("HELLO TEST DATA"))));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
    }

    @Test
    public void negativeReplicasStream() throws Exception {

        Response response = target("/sos/storage/stream")
                .request()
                .post(Entity.json(REQUEST_WITH_NEGATIVE_REPLICA_INFO.replace("{DATA}", IO.toBase64("HELLO TEST DATA"))));

        assertEquals(response.getStatus(), HTTPStatus.BAD_REQUEST);
    }

    @Test
    public void zeroReplicasStream() throws Exception {

        Response response = target("/sos/storage/stream")
                .request()
                .post(Entity.json(REQUEST_WITH_ZERO_REPLICA_INFO.replace("{DATA}", IO.toBase64("HELLO TEST DATA"))));

        assertEquals(response.getStatus(), HTTPStatus.BAD_REQUEST);
    }

    @Test
    public void excessiveReplicasStream() throws Exception {

        Response response = target("/sos/storage/stream")
                .request()
                .post(Entity.json(REQUEST_WITH_EXCESSIVE_REPLICA_INFO.replace("{DATA}", IO.toBase64("HELLO TEST DATA"))));

        assertEquals(response.getStatus(), HTTPStatus.BAD_REQUEST);
    }

    @Test
    public void challengeForNoAtomFails() throws GUIDGenerationException {

        Response response = target("/sos/storage/data/guid/SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4/challenge/NOT_IMPORTANT")
                .request().get();

        assertEquals(response.getStatus(), HTTPStatus.OK);
        IGUID guid = GUIDFactory.recreateGUID(response.readEntity(String.class));
        assertTrue(guid.isInvalid());
    }

    @Test
    public void challengeForAtomWorks() throws GUIDGenerationException, IOException {

        Response response = target("/sos/storage/stream")
                .request()
                .post(Entity.json(
                        BASIC_REQUEST.replace("{DATA}", IO.toBase64("data"))
                                .replace("{GUID}", "SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7")
                ));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        AtomManifest atomManifest = JSONHelper.JsonObjMapper().readValue(response.readEntity(String.class), AtomManifest.class);

        Response challengeResponse = target("/sos/storage/data/guid/" + atomManifest.guid().toMultiHash() + "/challenge/THIS_IS_MY_CHALLENGE")
                .request().get();

        assertEquals(challengeResponse.getStatus(), HTTPStatus.OK);
        assertEquals(challengeResponse.readEntity(String.class), "SHA256_16_83941ab394968e84292a106b8df43a0d9cc12b4481c2de85d56f9db483cde28b" /* Generated using https://quickhash.com/ */);
    }

    @Test
    public void noChallengeForAtomDoesNotWork() throws GUIDGenerationException, IOException {

        Response response = target("/sos/storage/stream")
                .request()
                .post(Entity.json(
                        BASIC_REQUEST.replace("{DATA}", IO.toBase64("data"))
                                .replace("{GUID}", "SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7")
                ));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        AtomManifest atomManifest = JSONHelper.JsonObjMapper().readValue(response.readEntity(String.class), AtomManifest.class);

        Response challengeResponse = target("/sos/storage/data/guid/" + atomManifest.guid().toMultiHash() + "/challenge/")
                .request().get();

        assertEquals(challengeResponse.getStatus(), HTTPStatus.BAD_REQUEST);
    }

    @Test
    public void spaceChallengeForAtomDoesNotWork() throws GUIDGenerationException, IOException {

        Response response = target("/sos/storage/stream")
                .request()
                .post(Entity.json(
                        BASIC_REQUEST.replace("{DATA}", IO.toBase64("data"))
                                .replace("{GUID}", "SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7")
                ));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        AtomManifest atomManifest = JSONHelper.JsonObjMapper().readValue(response.readEntity(String.class), AtomManifest.class);

        Response challengeResponse = target("/sos/storage/data/guid/" + atomManifest.guid().toMultiHash() + "/challenge/ ")
                .request().get();

        assertEquals(challengeResponse.getStatus(), HTTPStatus.BAD_REQUEST);
    }

}
