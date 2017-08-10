package uk.ac.standrews.cs.sos.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.HTTP.HTTPStatus;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTRMSTest extends CommonRESTTest {

    @Test
    public void addUserTest() {

        String userJSON = "" +
                "{\n" +
                "  \"GUID\" : \"SHA256_16_16eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\",\n" +
                "  \"Name\" : \"testUser\",\n" +
                "  \"Certificate\" : \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL1PuDAdgVaND5DN31SXXEtxQLx1nZ5WeM5uvC8+S0A1bKBqAUyiQALUEapYGYzzXt5XomPFVJLu/aUIuxg4nbcCAwEAAQ==\"\n" +
                "}";

        Response response = target("/usro/user")
                .request()
                .post(Entity.json(userJSON));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
    }

    @Test
    public void addRoleTest() {

        String roleJSON = "" +
                "{\n" +
                "  \"Certificate\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALTzc5FqmUeROVsjPDlFx5scqxXwasTg9E3Is+G6qPGtx5viUrk2djs2eAqYuGaAoQxDzaIz3jIwOZxwwhphWwMCAwEAAQ==\",\n" +
                "  \"GUID\": \"SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28\",\n" +
                "  \"Name\": \"testRole\",\n" +
                "  \"PublicKey\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMjM62sn4y46oTZfvANoYbRuxioZHPTNuRCO6NhVSSGFt/c7fl7ud/yxDu4TZMyQ1ACxTmcJeTpnhSX778RV9FkCAwEAAQ==\",\n" +
                "  \"Signature\": \"dCeoR22d9SgMClNLsrjngWhGehQ5u6JsCBCOqzYOT/XSVUg4WyPnapDoukhXl5rBnpq0KDcx2Iv3IA+5MZ7haA==\",\n" +
                "  \"User\": \"SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\"\n" +
                "}";

        Response response = target("/usro/role")
                .request()
                .post(Entity.json(roleJSON));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
    }

    @Test
    public void getUserTest() throws JSONException {

        String userJSON = "" +
                "{\n" +
                "  \"GUID\" : \"SHA256_16_16eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\",\n" +
                "  \"Name\" : \"testUser\",\n" +
                "  \"Certificate\" : \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL1PuDAdgVaND5DN31SXXEtxQLx1nZ5WeM5uvC8+S0A1bKBqAUyiQALUEapYGYzzXt5XomPFVJLu/aUIuxg4nbcCAwEAAQ==\"\n" +
                "}";

        target("/usro/user")
                .request()
                .post(Entity.json(userJSON));

        Response response = target("/usro/user/SHA256_16_16eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002").request().get();

        assertEquals(response.getStatus(), HTTPStatus.OK);
        JSONAssert.assertEquals(userJSON, response.readEntity(String.class), true);
    }

    @Test
    public void getRoleTest() throws JSONException {

        String roleJSON = "" +
                "{\n" +
                "  \"Certificate\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALTzc5FqmUeROVsjPDlFx5scqxXwasTg9E3Is+G6qPGtx5viUrk2djs2eAqYuGaAoQxDzaIz3jIwOZxwwhphWwMCAwEAAQ==\",\n" +
                "  \"GUID\": \"SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28\",\n" +
                "  \"Name\": \"testRole\",\n" +
                "  \"PublicKey\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMjM62sn4y46oTZfvANoYbRuxioZHPTNuRCO6NhVSSGFt/c7fl7ud/yxDu4TZMyQ1ACxTmcJeTpnhSX778RV9FkCAwEAAQ==\",\n" +
                "  \"Signature\": \"dCeoR22d9SgMClNLsrjngWhGehQ5u6JsCBCOqzYOT/XSVUg4WyPnapDoukhXl5rBnpq0KDcx2Iv3IA+5MZ7haA==\",\n" +
                "  \"User\": \"SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\"\n" +
                "}";

        target("/usro/role")
                .request()
                .post(Entity.json(roleJSON));

        Response response = target("/usro/role/SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28").request().get();

        assertEquals(response.getStatus(), HTTPStatus.OK);
        JSONAssert.assertEquals(roleJSON, response.readEntity(String.class), true);
    }

    @Test
    public void getUserRolesTest() throws JSONException, IOException {

        String role_1_JSON = "" +
                "{\n" +
                "  \"Certificate\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALTzc5FqmUeROVsjPDlFx5scqxXwasTg9E3Is+G6qPGtx5viUrk2djs2eAqYuGaAoQxDzaIz3jIwOZxwwhphWwMCAwEAAQ==\",\n" +
                "  \"GUID\": \"SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28\",\n" +
                "  \"Name\": \"testRole1\",\n" +
                "  \"PublicKey\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMjM62sn4y46oTZfvANoYbRuxioZHPTNuRCO6NhVSSGFt/c7fl7ud/yxDu4TZMyQ1ACxTmcJeTpnhSX778RV9FkCAwEAAQ==\",\n" +
                "  \"Signature\": \"dCeoR22d9SgMClNLsrjngWhGehQ5u6JsCBCOqzYOT/XSVUg4WyPnapDoukhXl5rBnpq0KDcx2Iv3IA+5MZ7haA==\",\n" +
                "  \"User\": \"SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\"\n" +
                "}";

        String role_2_JSON = "" +
                "{\n" +
                "  \"Certificate\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALTzc5FqmUeROVsjPDlFx5scqxXwasTg9E3Is+G6qPGtx5viUrk2djs2eAqYuGaAoQxDzaIz3jIwOZxwwhphWwMCAwEAAQ==\",\n" +
                "  \"GUID\": \"SHA256_16_222e632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28\",\n" +
                "  \"Name\": \"testRole2\",\n" +
                "  \"PublicKey\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMjM62sn4y46oTZfvANoYbRuxioZHPTNuRCO6NhVSSGFt/c7fl7ud/yxDu4TZMyQ1ACxTmcJeTpnhSX778RV9FkCAwEAAQ==\",\n" +
                "  \"Signature\": \"dCeoR22d9SgMClNLsrjngWhGehQ5u6JsCBCOqzYOT/XSVUg4WyPnapDoukhXl5rBnpq0KDcx2Iv3IA+5MZ7haA==\",\n" +
                "  \"User\": \"SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\"\n" +
                "}";

        target("/usro/role")
                .request()
                .post(Entity.json(role_1_JSON));

        target("/usro/role")
                .request()
                .post(Entity.json(role_2_JSON));

        Response response = target("/usro/user/SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002/roles").request().get();

        assertEquals(response.getStatus(), HTTPStatus.OK);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(response.readEntity(String.class));
        assertTrue(node.isArray());

        JsonNode role_1 = node.get(0);
        JsonNode role_2 = node.get(1);

        assertEquals(role_1.get("User").asText(), "SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002");
        assertEquals(role_2.get("User").asText(), "SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002");

        assertTrue(role_1.get("GUID").asText().equals("SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28") ||
                role_1.get("GUID").asText().equals("SHA256_16_222e632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28"));

        assertTrue(role_2.get("GUID").asText().equals("SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28") ||
                role_2.get("GUID").asText().equals("SHA256_16_222e632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28"));

        assertNotEquals(role_1.get("GUID").asText(), role_2.get("GUID").asText());
    }
}