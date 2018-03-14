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

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPStatus;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTNodeDiscoveryServiceTest extends CommonRESTTest {

    @Test
    public void testRegister() throws Exception {

        String data = "" +
                "{" +
                "    \"guid\": \"SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"," +
                "    \"type\" : \"Node\",\n"+
                "    \"certificate\" : \"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ+g1RiPTeaFIiw1LZwogFCwa3Cd4ECVMNKzG9lVxI0yonvj8mRyH4Z1/3WaoQTjuZ/lUGU43L24zpsmlxOcUkUCAwEAAQ==\",\n"+
                "    \"hostname\": \"Simones-MacBook-Pro.local\"," +
                "    \"port\": 8080," +
                "    \"services\": {" +
                "        \"storage\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"cms\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"mds\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"nds\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"rms\": {" +
                "            \"exposed\": true" +
                "        }," +
                "        \"mms\": {" +
                "            \"exposed\": true" +
                "        }" +
                "    }" +
                "}";

        Response response = target("/sos/nds/node")
                .request()
                .post(Entity.json(data));

        assertEquals(response.getStatus(), HTTPStatus.CREATED);
        JSONAssert.assertEquals(data, response.readEntity(String.class), true);

        response.close();
    }

}
