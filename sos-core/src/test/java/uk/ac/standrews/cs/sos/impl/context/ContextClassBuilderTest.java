package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextClassBuilder;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextClassBuilderTest {

    @Test
    public void basicClassConstruction() throws IOException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);
        String clazzString = ContextClassBuilder.ConstructClass(node);

        String MATCHING_CLAZZ =
                "package uk.ac.standrews.cs.sos.impl.context;\n" +
                        "\n" +
                        "import uk.ac.standrews.cs.guid.IGUID;\n" +
                        "import uk.ac.standrews.cs.logger.LEVEL;\n" +
                        "import uk.ac.standrews.cs.sos.impl.services.SOSAgent;\n" +
                        "import uk.ac.standrews.cs.sos.model.NodesCollection;\n" +
                        "import uk.ac.standrews.cs.sos.model.Policy;\n" +
                        "import uk.ac.standrews.cs.sos.model.SOSPredicate;\n" +
                        "import uk.ac.standrews.cs.sos.utils.SOS_LOG;\n" +
                        "import java.util.Collections;\n" +
                        "import java.util.Arrays;\n" +
                        "import com.fasterxml.jackson.databind.JsonNode;\n" +
                        "\n" +
                        "public class Test extends BaseContext {\n" +
                        "\n" +
                        "public Test (JsonNode jsonNode, PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {  \n" +
                        "super(jsonNode, policyActions, name, domain, codomain);\n" +
                        "}\n" +
                        "\n" +
                        "public Test (JsonNode jsonNode, PolicyActions policyActions, IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {  \n" +
                        "super(jsonNode, policyActions, guid, name, domain, codomain);\n" +
                        "}\n" +
                        "\n" +
                        "@Override\n" +
                        "public SOSPredicate predicate() {\n" +
                        "\n" +
                        "    return new P(PREDICATE_ALWAYS_TRUE);\n" +
                        "}\n" +
                        "\n" +
                        "class P extends SOSPredicateImpl {\n" +
                        "\n" +
                        "    P(long maxAge) {\n" +
                        "        super(maxAge);\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public boolean test(IGUID guid) {\n" +
                        "        return \n" +
                        "    }\n" +
                        "}@Override\n" +
                        "public Policy[] policies() {\n" +
                        "        return new Policy[]{  };\n" +
                        "}\n" +
                        "\n" +
                        "}\n";

        assertEquals(clazzString, MATCHING_CLAZZ);

    }

}