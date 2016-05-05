package uk.ac.standrews.cs.sos.rest;

import org.testng.annotations.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AddAtomTest extends BasicTest {

    private static final String TEST_HTTP_BIN_URL = "https://httpbin.org/range/10";

    @Test
    public void testAddAtomByLocation() {
        Response response = target("atom")
                .path("/location")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json("{\"Location\":\"" + TEST_HTTP_BIN_URL +"\"}"));

        assertEquals(response.getStatus(), Response.Status.ACCEPTED.getStatusCode());
        String body = response.readEntity(String.class);
        assertTrue(body.length() > 0); // TODO - this is a weak test
    }

    @Test
    public void testAddAtomByStream() {
        InputStream stream = new ByteArrayInputStream("Hello World!".getBytes(StandardCharsets.UTF_8));

        Response response = target("atom")
                .path("/stream")
                .request()
                .post(Entity.entity(stream, MediaType.MULTIPART_FORM_DATA_TYPE));

        assertEquals(response.getStatus(), Response.Status.ACCEPTED.getStatusCode());
        String body = response.readEntity(String.class);
        assertTrue(body.length() > 0); // TODO - this is a weak test
    }

}