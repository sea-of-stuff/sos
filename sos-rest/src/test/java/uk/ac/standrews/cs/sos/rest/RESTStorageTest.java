package uk.ac.standrews.cs.sos.rest;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.HTTP.HTTPStatus;
import uk.ac.standrews.cs.sos.rest.utils.HelperTest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTStorageTest extends CommonRESTTest {

    private final static String TEST_NODE_INFO =
            "{\n" +
                    "  \"Type\": \"Atom\",\n" +
                    "  \"GUID\": \"a17c9aaa61e80a1bf71d0d850af4e5baa9800bbd\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"Type\": \"persistent\",\n" +
                    "      \"Location\": \"sos://6b67f67f31908dd0e574699f163eda2cc117f7f4/a17c9aaa61e80a1bf71d0d850af4e5baa9800bbd\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    private final static String TEST_HTTPS_BIN_ATOM_MANIFEST =
            "{\n" +
                    "  \"Type\": \"Atom\",\n" +
                    "  \"GUID\": \"d68c19a0a345b7eab78d5e11e991c026ec60db63\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"Type\": \"provenance\",\n" +
                    "      \"Location\": \"https://httpbin.org/range/10\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"Type\": \"persistent\",\n" +
                    "      \"Location\": \"sos://6b67f67f31908dd0e574699f163eda2cc117f7f4/d68c19a0a345b7eab78d5e11e991c026ec60db63\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    private final static String TEST_HTTP_BIN_ATOM_MANIFEST =
            "{\n" +
                    "  \"Type\": \"Atom\",\n" +
                    "  \"GUID\": \"d68c19a0a345b7eab78d5e11e991c026ec60db63\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"Type\": \"provenance\",\n" +
                    "      \"Location\": \"http://httpbin.org/range/10\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"Type\": \"persistent\",\n" +
                    "      \"Location\": \"sos://6b67f67f31908dd0e574699f163eda2cc117f7f4/d68c19a0a345b7eab78d5e11e991c026ec60db63\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    private final static String TEST_EMPTY_ATOM_MANIFEST =
            "{\n" +
                    "  \"Type\": \"Atom\",\n" +
                    "  \"GUID\": \"da39a3ee5e6b4b0d3255bfef95601890afd80709\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"Type\": \"persistent\",\n" +
                    "      \"Location\": \"sos://6b67f67f31908dd0e574699f163eda2cc117f7f4/da39a3ee5e6b4b0d3255bfef95601890afd80709\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    @Test
    public void testStoreInputStream() throws Exception {

        InputStream testData = HelperTest.StringToInputStream("data");

        Response response = target("/storage/stream")
                .request()
                .post(Entity.entity(testData, MediaType.MULTIPART_FORM_DATA_TYPE));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), true);

        response.close();
    }

    @Test
    public void testStoreEmptyInputStream() throws Exception {

        InputStream testData = HelperTest.StringToInputStream("");

        target("/storage/stream")
                .request()
                .post(Entity.entity(testData, MediaType.MULTIPART_FORM_DATA_TYPE));

        Response response = target("/storage/stream")
                .request()
                .post(Entity.entity(testData, MediaType.MULTIPART_FORM_DATA_TYPE));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        JSONAssert.assertEquals(TEST_EMPTY_ATOM_MANIFEST, response.readEntity(String.class), true);

        response.close();
    }

    @Test (expectedExceptions = NullPointerException.class)
    public void testStoreNullInputStream() throws Exception {

        target("/storage/stream")
                .request()
                .post(Entity.entity(null, MediaType.MULTIPART_FORM_DATA_TYPE));
    }

    @Test
    public void testStoreViaHTTPURL() throws Exception {

        String data = "{\n" +
                "    \"uri\" : \"http://httpbin.org/range/10\"\n" +
                "}";

        Response response = target("/storage/uri")
                .request()
                .post(Entity.json(data));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        JSONAssert.assertEquals(TEST_HTTP_BIN_ATOM_MANIFEST, response.readEntity(String.class), true);

        response.close();
    }

    @Test
    public void testStoreViaHTTPSURL() throws Exception {

        String data = "{\n" +
                "    \"uri\" : \"https://httpbin.org/range/10\"\n" +
                "}";

        Response response = target("/storage/uri")
                .request()
                .post(Entity.json(data));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        JSONAssert.assertEquals(TEST_HTTPS_BIN_ATOM_MANIFEST, response.readEntity(String.class), true);

        response.close();
    }

}
