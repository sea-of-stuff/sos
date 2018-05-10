/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.json;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollectionType;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextDeserializerTest extends SetUpTest {

    @Test
    public void basicContextDeserialization() throws IOException, GUIDGenerationException {

        // FIXME - This node's GUID change in between the tests

        String contextJSON = "" +
                "{\n" +
                "  \"type\": \"Context\",\n" +
                "  \"name\": \"TEST\",\n" +
                "  \"invariant\": \"SHA256_16_1b94e313eecb5c13a2a3a33bb6cae3145703dd1aeb0f5c1d461decb32684564b\",\n" +
                "  \"content\": \"SHA256_16_e85f9770df500fb74794d429dd8d32238340c845fdac48bb17fb6a87bde86547\",\n" +
                "  \"domain\": {\n" +
                "    \"type\": \"LOCAL\",\n" +
                "    \"nodes\": []\n" +
                "  },\n" +
                "  \"codomain\": {\n" +
                "    \"type\": \"SPECIFIED\",\n" +
                "    \"nodes\": [\"SHA256_16_29497892317a98d1299808516f7456fae992b88b2e50682ce31ff25c76f02caa\", \"SHA256_16_d3720e9346c08abaf7017b57cab422fc6ae7055886162bb92ca4a6cbb386c0d1\"]\n" +
                "  },\n" +
                "  \"predicate\": \"SHA256_16_57daa6858e8bdcc0e2e1ab93a1a782f2cd566186aff620fe0e7d1a545d681cab\",\n" +
                "  \"policies\": [\"SHA256_16_d9e4b085724893ff91d4666cd0fc63dbf98fd38b1e05952dc7b836ece28d2a84\", \"SHA256_16_acad290a502ea13384879c68d9cc481604190c46a31508ccf8bb93a4a74ee8e2\"]\n" +
                "}";

        Context context = JSONHelper.jsonObjMapper().readValue(contextJSON, Context.class);
        assertNotNull(context);
        assertEquals(context.guid().toMultiHash(), "SHA256_16_4a4aeb9ae708ef189b49c795d89474c5b4da9dc05fd1f6de8068ce662c42d0cb");
        assertEquals(context.invariant().toMultiHash(), "SHA256_16_df5262037451b55a907d370823c6f5caedd59fadfdfc2df670cf52e195262ef2");
        assertEquals(context.getName(), "TEST");
        assertEquals(context.predicate().toMultiHash(), "SHA256_16_57daa6858e8bdcc0e2e1ab93a1a782f2cd566186aff620fe0e7d1a545d681cab");
        assertTrue(context.policies().contains(GUIDFactory.recreateGUID("SHA256_16_d9e4b085724893ff91d4666cd0fc63dbf98fd38b1e05952dc7b836ece28d2a84")));
        assertTrue(context.policies().contains(GUIDFactory.recreateGUID("SHA256_16_acad290a502ea13384879c68d9cc481604190c46a31508ccf8bb93a4a74ee8e2")));

        assertNotNull(context.domain(false));
        assertEquals(context.domain(false).type(), NodesCollectionType.LOCAL);

        assertNotNull(context.codomain());
        assertEquals(context.codomain().type(), NodesCollectionType.SPECIFIED);
        assertTrue(context.codomain().nodesRefs().contains(GUIDFactory.recreateGUID("SHA256_16_29497892317a98d1299808516f7456fae992b88b2e50682ce31ff25c76f02caa")));
        assertTrue(context.codomain().nodesRefs().contains(GUIDFactory.recreateGUID("SHA256_16_d3720e9346c08abaf7017b57cab422fc6ae7055886162bb92ca4a6cbb386c0d1")));

        assertEquals(context.content().toMultiHash(), "SHA256_16_e85f9770df500fb74794d429dd8d32238340c845fdac48bb17fb6a87bde86547");
    }
}