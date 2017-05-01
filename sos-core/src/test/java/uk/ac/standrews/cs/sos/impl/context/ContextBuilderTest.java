package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextBuilderTest {

    @Test
    public void basicClassConstruction() throws IOException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\",\n" +
                        "    \"dependencies\": []\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);
        String clazzString = ContextBuilder.ConstructClass(node);

        String MATCHING_CLAZZ =
                "package uk.ac.standrews.cs.sos.impl.context\n" +
                        "\n" +
                        "import uk.ac.standrews.cs.sos.model.*;\n" +
                        "\n" +
                        "public class Test extends CommonContext {\n" +
                        "public Test () {\n" +
                        "}\n" +
                        "\n" +
                        "@Override\n" +
                        "public SOSPredicate predicate() {\n" +
                        "\n" +
                        "    SOSAgent agent = SOSAgent.instance();\n" +
                        "\n" +
                        "    return new SOSPredicateImpl(p -> {\n" +
                        "        try {\n" +
                        "            _PREDICATE_TAG_\n" +
                        "        } catch (Exception e) {\n" +
                        "            SOS_LOG.log(LEVEL.ERROR, \"Predicate could not be run\");\n" +
                        "        }\n" +
                        "\n" +
                        "        return false;\n" +
                        "    });\n" +
                        "}\n" +
                        "\n" +
                        "@Override\n" +
                        "public Policy[] policies() {\n" +
                        "        return new Policy[]{ _POLICIES_ }\n" +
                        "}\n" +
                        "\n" +
                        "}\n";

        assertEquals(clazzString, MATCHING_CLAZZ);

    }

}