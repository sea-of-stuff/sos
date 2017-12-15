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

        String contextJSON = "" +
                "{\n" +
                "  \"type\": \"Context\",\n" +
                "  \"GUID\": \"SHA256_16_ebe6cc0147e8cf26d154c852b2a58faa7063b5a75e908b4574ac5e4ae278b0f9\",\n" +
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

        Context context = JSONHelper.JsonObjMapper().readValue(contextJSON, Context.class);
        assertNotNull(context);
        assertEquals(context.guid().toMultiHash(), "SHA256_16_ebe6cc0147e8cf26d154c852b2a58faa7063b5a75e908b4574ac5e4ae278b0f9");
        assertEquals(context.invariant().toMultiHash(), "SHA256_16_1b94e313eecb5c13a2a3a33bb6cae3145703dd1aeb0f5c1d461decb32684564b");
        assertEquals(context.getName(), "TEST");
        assertEquals(context.predicate().toMultiHash(), "SHA256_16_57daa6858e8bdcc0e2e1ab93a1a782f2cd566186aff620fe0e7d1a545d681cab");
        assertTrue(context.policies().contains(GUIDFactory.recreateGUID("SHA256_16_d9e4b085724893ff91d4666cd0fc63dbf98fd38b1e05952dc7b836ece28d2a84")));
        assertTrue(context.policies().contains(GUIDFactory.recreateGUID("SHA256_16_acad290a502ea13384879c68d9cc481604190c46a31508ccf8bb93a4a74ee8e2")));

        assertNotNull(context.domain());
        assertEquals(context.domain().type(), NodesCollectionType.LOCAL);

        assertNotNull(context.codomain());
        assertEquals(context.codomain().type(), NodesCollectionType.SPECIFIED);
        assertTrue(context.codomain().nodesRefs().contains(GUIDFactory.recreateGUID("SHA256_16_29497892317a98d1299808516f7456fae992b88b2e50682ce31ff25c76f02caa")));
        assertTrue(context.codomain().nodesRefs().contains(GUIDFactory.recreateGUID("SHA256_16_d3720e9346c08abaf7017b57cab422fc6ae7055886162bb92ca4a6cbb386c0d1")));

        assertEquals(context.content().toMultiHash(), "SHA256_16_e85f9770df500fb74794d429dd8d32238340c845fdac48bb17fb6a87bde86547");
    }
}