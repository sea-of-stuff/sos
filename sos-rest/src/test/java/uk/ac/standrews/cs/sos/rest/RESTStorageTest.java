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
                    "  \"GUID\": \"SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"Type\": \"persistent\",\n" +
                    "      \"Location\": \"sos://SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4/SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    private final static String TEST_HTTPS_BIN_ATOM_MANIFEST =
            "{\n" +
                    "  \"Type\": \"Atom\",\n" +
                    "  \"GUID\": \"SHA256_16_72399361da6a7754fec986dca5b7cbaf1c810a28ded4abaf56b2106d06cb78b0\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"Type\": \"provenance\",\n" +
                    "      \"Location\": \"https://httpbin.org/range/10\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"Type\": \"persistent\",\n" +
                    "      \"Location\": \"sos://SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4/SHA256_16_72399361da6a7754fec986dca5b7cbaf1c810a28ded4abaf56b2106d06cb78b0\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    private final static String TEST_HTTP_BIN_ATOM_MANIFEST =
            "{\n" +
                    "  \"Type\": \"Atom\",\n" +
                    "  \"GUID\": \"SHA256_16_72399361da6a7754fec986dca5b7cbaf1c810a28ded4abaf56b2106d06cb78b0\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"Type\": \"provenance\",\n" +
                    "      \"Location\": \"http://httpbin.org/range/10\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"Type\": \"persistent\",\n" +
                    "      \"Location\": \"sos://SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4/SHA256_16_72399361da6a7754fec986dca5b7cbaf1c810a28ded4abaf56b2106d06cb78b0\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    private final static String TEST_EMPTY_ATOM_MANIFEST =
            "{\n" +
                    "  \"Type\": \"Atom\",\n" +
                    "  \"GUID\": \"SHA256_16_e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\",\n" +
                    "  \"Locations\": [\n" +
                    "    {\n" +
                    "      \"Type\": \"persistent\",\n" +
                    "      \"Location\": \"sos://SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4/SHA256_16_e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\"\n" +
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
        JSONAssert.assertEquals(TEST_HTTP_BIN_ATOM_MANIFEST, response.readEntity(String.class), false);

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
        JSONAssert.assertEquals(TEST_HTTPS_BIN_ATOM_MANIFEST, response.readEntity(String.class), false);

        response.close();
    }

}
