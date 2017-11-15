package uk.ac.standrews.cs.sos.impl.json;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.impl.context.ContextManifest;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.NodesCollectionType;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextSerializerTest {

    @Test
    public void basicContextSerializer() throws NodesCollectionException, GUIDGenerationException {

        String expectedContextJSON = "" +
                "{\n" +
                "  \"type\": \"Context\",\n" +
                "  \"GUID\": \"SHA256_16_40ebecbfc325d9b15040dc7b1134c57411d5273b9d7dacca3a0565eca05be2c9\",\n" +
                "  \"name\": \"TEST\",\n" +
                "  \"invariant\": \"SHA256_16_8a00a5e5770f21b5456de1c6068ba805154408e1c1a275377c64fa21825414fb\",\n" +
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
                "  \"max_age\": 1,\n" +
                "  \"policies\": [\"SHA256_16_d9e4b085724893ff91d4666cd0fc63dbf98fd38b1e05952dc7b836ece28d2a84\", \"SHA256_16_acad290a502ea13384879c68d9cc481604190c46a31508ccf8bb93a4a74ee8e2\"]\n" +
                "}";

        NodesCollection domain = new NodesCollectionImpl(NodesCollectionType.LOCAL);

        Set<IGUID> codomainRefs = new LinkedHashSet<>();
        codomainRefs.add(GUIDFactory.recreateGUID("SHA256_16_29497892317a98d1299808516f7456fae992b88b2e50682ce31ff25c76f02caa"));
        codomainRefs.add(GUIDFactory.recreateGUID("SHA256_16_d3720e9346c08abaf7017b57cab422fc6ae7055886162bb92ca4a6cbb386c0d1"));
        NodesCollection codomain = new NodesCollectionImpl(codomainRefs);

        IGUID predicate = GUIDFactory.recreateGUID("SHA256_16_57daa6858e8bdcc0e2e1ab93a1a782f2cd566186aff620fe0e7d1a545d681cab");

        Set<IGUID> policies = new LinkedHashSet<>();
        policies.add(GUIDFactory.recreateGUID("SHA256_16_d9e4b085724893ff91d4666cd0fc63dbf98fd38b1e05952dc7b836ece28d2a84"));
        policies.add(GUIDFactory.recreateGUID("SHA256_16_acad290a502ea13384879c68d9cc481604190c46a31508ccf8bb93a4a74ee8e2"));

        IGUID content = GUIDFactory.recreateGUID("SHA256_16_e85f9770df500fb74794d429dd8d32238340c845fdac48bb17fb6a87bde86547");

        Context basicContext = new ContextManifest("TEST", domain, codomain, predicate, 1, policies, null, content);

        JSONAssert.assertEquals(expectedContextJSON, basicContext.toString(), false);
    }
}