package uk.ac.standrews.cs.sos.rest.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPStatus;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.constants.JSONConstants.KEY_GUID;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTCMSTest extends CommonRESTTest {

    @Test
    public void addFATContextTest() throws GUIDGenerationException, IOException {

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
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"max_age\": 0,\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\"\n" +
                "\t}]\n" +
                "}";

        Response response = target("/sos/cms/context")
                .request()
                .post(Entity.json(FATContext));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        String guidS = JSONHelper.jsonObjMapper().readTree(response.readEntity(String.class)).get(KEY_GUID).asText();
        assertEquals(GUIDFactory.recreateGUID(guidS), GUIDFactory.recreateGUID("SHA256_16_9f103cdeefcff6affbb75b79b8b41bbe0965186b99ae20c81a5f0abeb8448b05"));
    }

    @Test
    public void addFATContextTestV2() throws GUIDGenerationException, IOException {

        String FATContext = "{\n" +
                "  \"context\" : {\n" +
                "    \"name\" : \"predicate_2\",\n" +
                "    \"domain\" : {\n" +
                "      \"type\" : \"SPECIFIED\",\n" +
                "      \"nodes\" : [ \"SHA256_16_aed7bbf1e6ef5c8d22162c096ab069b8d2056696be262551951660aac6d836ef\" ]\n" +
                "    },\n" +
                "    \"codomain\" : {\n" +
                "      \"type\" : \"LOCAL\",\n" +
                "      \"nodes\" : [ ]\n" +
                "    },\n" +
                "    \"max_age\" : 0\n" +
                "  },\n" +
                "  \"predicate\" : {\n" +
                "    \"type\" : \"Predicate\",\n" +
                "    \"predicate\" : \"CommonPredicates.TextOccurrencesIgnoreCase(guid, \\\"the\\\") == 1;\"\n" +
                "  },\n" +
                "  \"policies\" : [ ]\n" +
                "}";

        Response response = target("/sos/cms/context")
                .request()
                .post(Entity.json(FATContext));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        String guidS = JSONHelper.jsonObjMapper().readTree(response.readEntity(String.class)).get(KEY_GUID).asText();
        assertEquals(GUIDFactory.recreateGUID(guidS), GUIDFactory.recreateGUID("SHA256_16_66128d7bebd52f9d711c90e883c5de3ef831bf84a245dc9d0e3671965c0a3195"));
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
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"max_age\": 0,\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\"\n" +
                "\t}]\n" +
                "}";

        Response response = target("/sos/cms/context")
                .request()
                .post(Entity.json(FATContext));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        String guidS = JSONHelper.jsonObjMapper().readTree(response.readEntity(String.class)).get(KEY_GUID).asText();
        IGUID contextGUID = GUIDFactory.recreateGUID(guidS);
        assertFalse(contextGUID.isInvalid());

        Response response2 = target("/sos/cms/guid/" + contextGUID.toMultiHash()).request().get();
        assertEquals(response2.getStatus(), HTTPStatus.OK);
        String contextStringFormat = response2.readEntity(String.class);
        Context context = JSONHelper.jsonObjMapper().readValue(contextStringFormat, Context.class);
        assertNotNull(context);
    }

    @Test
    public void getContextsTest() throws IOException {

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
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"max_age\": 0,\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\"\n" +
                "\t}]\n" +
                "}";

        Response response = target("/sos/cms/context")
                .request()
                .post(Entity.json(FATContext));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);

        Response response2 = target("/sos/cms/contexts").request().get();
        assertEquals(response2.getStatus(), HTTPStatus.OK);
        String arrayOfContexts = response2.readEntity(String.class);
        JsonNode node = JSONHelper.jsonObjMapper().readTree(arrayOfContexts);
        assertTrue(node.isArray());
        assertEquals(node.size(), 1);
    }

    @Test
    public void addedContextHasProperGUID_1() throws IOException {

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
                "\t\t},\n" +
                "\t\t\"max_age\": 0\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"policies\": [{\n" +
                "\t\t\"type\": \"Policy\",\n" +
                "\t\t\"apply\": \"\",\n" +
                "\t\t\"satisfied\": \"return true;\"\n" +
                "\t}]\n" +
                "}";

        Response response = target("/sos/cms/context")
                .request()
                .post(Entity.json(FATContext));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        String guidS = JSONHelper.jsonObjMapper().readTree(response.readEntity(String.class)).get(KEY_GUID).asText();
        // See ContextBuilderTest - addFATContext
        assertEquals(guidS, "SHA256_16_9f103cdeefcff6affbb75b79b8b41bbe0965186b99ae20c81a5f0abeb8448b05");
    }

    @Test
    public void addedContextHasProperGUID_2() throws IOException {

        String FATContext = "{\n" +
                "  \"context\": {\n" +
                "    \"name\": \"predicate_2\",\n" +
                "    \"domain\": {\n" +
                "        \"type\": \"SPECIFIED\",\n" +
                "        \"nodes\": [\"SHA256_16_aed7bbf1e6ef5c8d22162c096ab069b8d2056696be262551951660aac6d836ef\"]\n" +
                "    },\n" +
                "    \"codomain\": {\n" +
                "      \"type\": \"LOCAL\",\n" +
                "      \"nodes\": []\n" +
                "    },\n" +
                "    \"max_age\": 0\n" +
                "  },\n" +
                "  \"predicate\": {\n" +
                "    \"type\": \"Predicate\",\n" +
                "    \"predicate\": \"CommonPredicates.TextOccurrencesIgnoreCase(guid, \\\"the\\\") == 1;\"\n" +
                "  },\n" +
                "  \"policies\": []\n" +
                "}\n";

        Response response = target("/sos/cms/context")
                .request()
                .post(Entity.json(FATContext));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        String guidS = JSONHelper.jsonObjMapper().readTree(response.readEntity(String.class)).get(KEY_GUID).asText();
        assertEquals(guidS, "SHA256_16_66128d7bebd52f9d711c90e883c5de3ef831bf84a245dc9d0e3671965c0a3195");
    }

    @Test
    public void deleteContextTest() throws ContextException {

        String contextJSON = "{\n" +
                "\t\"context\": {\n" +
                "\t\t\"name\": \"All\",\n" +
                "\t\t\"domain\": {\n" +
                "\t\t\t\"type\": \"LOCAL\",\n" +
                "\t\t\t\"nodes\": []\n" +
                "\t\t},\n" +
                "\t\t\"codomain\": {\n" +
                "\t\t\t\"type\": \"LOCAL\",\n" +
                "\t\t\t\"nodes\": []\n" +
                "\t\t},\n" +
                "\t\t\"max_age\": 0\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"policies\": []\n" +
                "}";

        IGUID guidContext = state.sos.getCMS().addContext(contextJSON);
        Context context = state.sos.getCMS().getContext(guidContext);

        Response response = target("/sos/cms/invariant/" + context.invariant().toMultiHash() + "/delete").request().get();
        assertEquals(response.getStatus(), HTTPStatus.OK);

        try {
            state.sos.getCMS().getContext(guidContext);
            assertTrue(false);
        } catch (ContextNotFoundException e) {
            assertTrue(true);
        }
    }

}
