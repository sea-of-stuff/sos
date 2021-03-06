/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module rest.
 *
 * rest is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * rest is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with rest. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.rest.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPStatus;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTUSROTest extends CommonRESTTest {

    @Test
    public void addUserTest() {

        String userJSON = "" +
                "{\n" +
                "  \"guid\" : \"SHA256_16_16eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\",\n" +
                "  \"name\" : \"testUser\",\n" +
                "  \"certificate\" : \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL1PuDAdgVaND5DN31SXXEtxQLx1nZ5WeM5uvC8+S0A1bKBqAUyiQALUEapYGYzzXt5XomPFVJLu/aUIuxg4nbcCAwEAAQ==\"\n" +
                "}";

        Response response = target("/sos/usro/user")
                .request()
                .post(Entity.json(userJSON));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
    }

    @Test
    public void addRoleTest() {

        String roleJSON = "" +
                "{\n" +
                "  \"certificate\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALTzc5FqmUeROVsjPDlFx5scqxXwasTg9E3Is+G6qPGtx5viUrk2djs2eAqYuGaAoQxDzaIz3jIwOZxwwhphWwMCAwEAAQ==\",\n" +
                "  \"guid\": \"SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28\",\n" +
                "  \"name\": \"testRole\",\n" +
                "  \"public_key\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMjM62sn4y46oTZfvANoYbRuxioZHPTNuRCO6NhVSSGFt/c7fl7ud/yxDu4TZMyQ1ACxTmcJeTpnhSX778RV9FkCAwEAAQ==\",\n" +
                "  \"signature\": \"dCeoR22d9SgMClNLsrjngWhGehQ5u6JsCBCOqzYOT/XSVUg4WyPnapDoukhXl5rBnpq0KDcx2Iv3IA+5MZ7haA==\",\n" +
                "  \"user\": \"SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\"\n" +
                "}";

        Response response = target("/sos/usro/role")
                .request()
                .post(Entity.json(roleJSON));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
    }

    @Test
    public void getUserTest() throws JSONException {

        String userJSON = "" +
                "{\n" +
                "  \"guid\" : \"SHA256_16_16eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\",\n" +
                "  \"type\" : \"User\",\n" +
                "  \"name\" : \"testUser\",\n" +
                "  \"certificate\" : \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL1PuDAdgVaND5DN31SXXEtxQLx1nZ5WeM5uvC8+S0A1bKBqAUyiQALUEapYGYzzXt5XomPFVJLu/aUIuxg4nbcCAwEAAQ==\"\n" +
                "}";

        target("/sos/usro/user")
                .request()
                .post(Entity.json(userJSON));

        Response response = target("/sos/usro/guid/SHA256_16_16eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002").request().get();

        assertEquals(response.getStatus(), HTTPStatus.OK);
        JSONAssert.assertEquals(userJSON, response.readEntity(String.class), true);
    }

    @Test
    public void getRoleTest() throws JSONException {

        String roleJSON = "" +
                "{\n" +
                "  \"certificate\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALTzc5FqmUeROVsjPDlFx5scqxXwasTg9E3Is+G6qPGtx5viUrk2djs2eAqYuGaAoQxDzaIz3jIwOZxwwhphWwMCAwEAAQ==\",\n" +
                "  \"guid\": \"SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28\",\n" +
                "  \"type\" : \"Role\",\n" +
                "  \"name\": \"testRole\",\n" +
                "  \"public_key\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMjM62sn4y46oTZfvANoYbRuxioZHPTNuRCO6NhVSSGFt/c7fl7ud/yxDu4TZMyQ1ACxTmcJeTpnhSX778RV9FkCAwEAAQ==\",\n" +
                "  \"signature\": \"dCeoR22d9SgMClNLsrjngWhGehQ5u6JsCBCOqzYOT/XSVUg4WyPnapDoukhXl5rBnpq0KDcx2Iv3IA+5MZ7haA==\",\n" +
                "  \"user\": \"SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\"\n" +
                "}";

        target("/sos/usro/role")
                .request()
                .post(Entity.json(roleJSON));

        Response response = target("/sos/usro/guid/SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28").request().get();

        assertEquals(response.getStatus(), HTTPStatus.OK);
        JSONAssert.assertEquals(roleJSON, response.readEntity(String.class), true);
    }

    @Test
    public void getUserRolesTest() throws IOException {

        String role_1_JSON = "" +
                "{\n" +
                "  \"certificate\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALTzc5FqmUeROVsjPDlFx5scqxXwasTg9E3Is+G6qPGtx5viUrk2djs2eAqYuGaAoQxDzaIz3jIwOZxwwhphWwMCAwEAAQ==\",\n" +
                "  \"guid\": \"SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28\",\n" +
                "  \"type\" : \"Role\",\n" +
                "  \"name\": \"testRole1\",\n" +
                "  \"public_key\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMjM62sn4y46oTZfvANoYbRuxioZHPTNuRCO6NhVSSGFt/c7fl7ud/yxDu4TZMyQ1ACxTmcJeTpnhSX778RV9FkCAwEAAQ==\",\n" +
                "  \"signature\": \"dCeoR22d9SgMClNLsrjngWhGehQ5u6JsCBCOqzYOT/XSVUg4WyPnapDoukhXl5rBnpq0KDcx2Iv3IA+5MZ7haA==\",\n" +
                "  \"user\": \"SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\"\n" +
                "}";

        String role_2_JSON = "" +
                "{\n" +
                "  \"certificate\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALTzc5FqmUeROVsjPDlFx5scqxXwasTg9E3Is+G6qPGtx5viUrk2djs2eAqYuGaAoQxDzaIz3jIwOZxwwhphWwMCAwEAAQ==\",\n" +
                "  \"guid\": \"SHA256_16_222e632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28\",\n" +
                "  \"type\" : \"Role\",\n" +
                "  \"name\": \"testRole2\",\n" +
                "  \"public_key\": \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMjM62sn4y46oTZfvANoYbRuxioZHPTNuRCO6NhVSSGFt/c7fl7ud/yxDu4TZMyQ1ACxTmcJeTpnhSX778RV9FkCAwEAAQ==\",\n" +
                "  \"signature\": \"dCeoR22d9SgMClNLsrjngWhGehQ5u6JsCBCOqzYOT/XSVUg4WyPnapDoukhXl5rBnpq0KDcx2Iv3IA+5MZ7haA==\",\n" +
                "  \"user\": \"SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002\"\n" +
                "}";

        target("/sos/usro/role")
                .request()
                .post(Entity.json(role_1_JSON));

        target("/sos/usro/role")
                .request()
                .post(Entity.json(role_2_JSON));

        Response response = target("/sos/usro/user/SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002/roles").request().get();

        assertEquals(response.getStatus(), HTTPStatus.OK);

        JsonNode node = JSONHelper.jsonObjMapper().readTree(response.readEntity(String.class));
        assertTrue(node.isArray());

        JsonNode role_1 = node.get(0);
        JsonNode role_2 = node.get(1);

        assertEquals(role_1.get("user").asText(), "SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002");
        assertEquals(role_2.get("user").asText(), "SHA256_16_06eec56d4a34310e3026b1c949f6ee9954e515d1a5dd734a1675eb21f8655002");

        assertTrue(role_1.get("guid").asText().equals("SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28") ||
                role_1.get("guid").asText().equals("SHA256_16_222e632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28"));

        assertTrue(role_2.get("guid").asText().equals("SHA256_16_4dce632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28") ||
                role_2.get("guid").asText().equals("SHA256_16_222e632041a51e6c16f784dc6da9be05c1d511ebec9c49f30debb53d4751ab28"));

        assertNotEquals(role_1.get("guid").asText(), role_2.get("guid").asText());
    }
}