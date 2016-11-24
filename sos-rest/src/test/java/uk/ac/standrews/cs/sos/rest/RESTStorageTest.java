package uk.ac.standrews.cs.sos.rest;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.ac.standrews.cs.sos.HTTP.HTTPState;
import uk.ac.standrews.cs.sos.rest.utils.HelperTest;

import javax.ws.rs.ProcessingException;
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
            "  \"ContentGUID\": \"a17c9aaa61e80a1bf71d0d850af4e5baa9800bbd\",\n" +
            "  \"Locations\": [\n" +
            "    {\n" +
            "      \"Type\": \"persistent\",\n" +
            "      \"Location\": \"sos://3c9bfd93ab9a6e2ed501fc583685088cca66bac2/a17c9aaa61e80a1bf71d0d850af4e5baa9800bbd\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private final static String TEST_HTTP_BIN_ATOM_MANIFEST =
            "{\n" +
            "  \"Type\": \"Atom\",\n" +
            "  \"ContentGUID\": \"fc8ef58da68b3d753343f4bea07b95bf71fa3850\",\n" +
            "  \"Locations\": [\n" +
            "    {\n" +
            "      \"Type\": \"provenance\",\n" +
            "      \"Location\": \"https://httpbin.org/stream/10\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"Type\": \"persistent\",\n" +
            "      \"Location\": \"sos://3c9bfd93ab9a6e2ed501fc583685088cca66bac2/fc8ef58da68b3d753343f4bea07b95bf71fa3850\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void testStoreInputStream() throws Exception {

        InputStream testData = HelperTest.StringToInputStream("data");

        Response response = target("/storage/stream")
                .request()
                .post(Entity.entity(testData, MediaType.MULTIPART_FORM_DATA_TYPE));

        assertEquals(response.getStatus(), HTTPState.CREATED);
        JSONAssert.assertEquals(TEST_NODE_INFO, response.readEntity(String.class), true);

    }

    @Test (expected = ProcessingException.class)
    public void testStoreEmptyInputStream() throws Exception {

        InputStream testData = HelperTest.StringToInputStream("");

        target("/storage/stream")
                .request()
                .post(Entity.entity(testData, MediaType.MULTIPART_FORM_DATA_TYPE));
    }

    @Test
    public void testStoreViaURL() throws Exception {

        String data = "{\n" +
                "    \"uri\" : \"https://httpbin.org/stream/10\"\n" +
                "}";

        Response response = target("/storage/uri")
                .request()
                .post(Entity.json(data));

        assertEquals(response.getStatus(), HTTPState.CREATED);
        JSONAssert.assertEquals(TEST_HTTP_BIN_ATOM_MANIFEST, response.readEntity(String.class), true);

    }

}
