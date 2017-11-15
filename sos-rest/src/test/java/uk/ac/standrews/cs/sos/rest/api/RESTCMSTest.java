package uk.ac.standrews.cs.sos.rest.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPStatus;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTCMSTest extends CommonRESTTest {

    @Test
    public void addFATContextTest() throws GUIDGenerationException {

        String FATContext = "{\n" +
                "\t\"context\": {\n" +
                "\t\t\"name\": \"All\",\n" +
                "\t\t\"domain\": {\n" +
                "\t\t\t\"type\": \"LOCAL\",\n" +
                "\t\t\t\"nodes\": []\n" +
                "\t\t},\n" +
                "\t\t\"codomain\": {\n" +
                "\t\t\t\"type\": \"SPECIFIED\",\n" +
                "\t\t\t\"nodes\": [\"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"]\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t},\n" +
                "\t\"max_age\": 0,\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t}]\n" +
                "}";

        Response response = target("/sos/cms/context")
                .request()
                .post(Entity.json(FATContext));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        String guidS = response.readEntity(String.class);
        assertEquals(GUIDFactory.recreateGUID(guidS), GUIDFactory.recreateGUID("SHA256_16_e9561e61c65158f11fcf3d553ba0045a882f340ba6461480c2aab7feef4c672e"));
    }

    @Test
    public void getContextTest() throws GUIDGenerationException, IOException {

        String FATContext = "{\n" +
                "\t\"context\": {\n" +
                "\t\t\"name\": \"All\",\n" +
                "\t\t\"domain\": {\n" +
                "\t\t\t\"type\": \"LOCAL\",\n" +
                "\t\t\t\"nodes\": []\n" +
                "\t\t},\n" +
                "\t\t\"codomain\": {\n" +
                "\t\t\t\"type\": \"SPECIFIED\",\n" +
                "\t\t\t\"nodes\": [\"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"]\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t},\n" +
                "\t\"max_age\": 0,\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t}]\n" +
                "}";

        Response response = target("/sos/cms/context")
                .request()
                .post(Entity.json(FATContext));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        String guidS = response.readEntity(String.class);
        IGUID contextGUID = GUIDFactory.recreateGUID(guidS);

        Response response2 = target("/sos/cms/guid/" + contextGUID.toMultiHash()).request().get();
        assertEquals(response2.getStatus(), HTTPStatus.OK);
        String contextStringFormat = response2.readEntity(String.class);
        Context context = JSONHelper.JsonObjMapper().readValue(contextStringFormat, Context.class);
        assertNotNull(context);
    }

    @Test
    public void getContextsTest() throws GUIDGenerationException, IOException {

        String FATContext = "{\n" +
                "\t\"context\": {\n" +
                "\t\t\"name\": \"All\",\n" +
                "\t\t\"domain\": {\n" +
                "\t\t\t\"type\": \"LOCAL\",\n" +
                "\t\t\t\"nodes\": []\n" +
                "\t\t},\n" +
                "\t\t\"codomain\": {\n" +
                "\t\t\t\"type\": \"SPECIFIED\",\n" +
                "\t\t\t\"nodes\": [\"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"]\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t},\n" +
                "\t\"max_age\": 0,\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\",\n" +
                "\t\t\"dependencies\": []\n" +
                "\t}]\n" +
                "}";

        Response response = target("/sos/cms/context")
                .request()
                .post(Entity.json(FATContext));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);

        Response response2 = target("/sos/cms/contexts").request().get();
        assertEquals(response2.getStatus(), HTTPStatus.OK);
        String arrayOfContexts = response2.readEntity(String.class);
        JsonNode node = JSONHelper.JsonObjMapper().readTree(arrayOfContexts);
        assertTrue(node.isArray());
        assertEquals(node.size(), 1);
    }

}
