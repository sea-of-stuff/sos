package uk.ac.standrews.cs.sos.rest;

import org.testng.annotations.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AddAtomPerformanceTest extends BasicPerformanceTest {

    private static final String UNSPLASH_URL = "https://unsplash.it/1024/1024?image=";

    @Test
    public void testAddAtomByLocation() {

        for (int i = 400; i < 410; i++) {
            long start = System.currentTimeMillis();
            Response response = target("atom")
                    .path("/location")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.json("{\"Location\":\"" + UNSPLASH_URL + Integer.toString(i) + "\"}"));
            long end = System.currentTimeMillis();
            System.out.println((end - start) / 1000.0);

            assertEquals(response.getStatus(), Response.Status.ACCEPTED.getStatusCode());
        }
    }
}
