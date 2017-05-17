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
                        "    \"name\": \"Test\",\n" +
                        "    \"dependencies\": []\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);
        String clazzString = ContextClassBuilder.ConstructClass(node);

        String MATCHING_CLAZZ =
                "package uk.ac.standrews.cs.sos.impl.context;\n" +
                        "\n" +
                        "import uk.ac.standrews.cs.sos.model.*;\n" +
                        "import uk.ac.standrews.cs.sos.model.SOSPredicate;\n" +
                        "import uk.ac.standrews.cs.LEVEL;\n" +
                        "import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;\n" +
                        "import uk.ac.standrews.cs.sos.utils.SOS_LOG;\n" +
                        "\n" +
                        "public class Test extends CommonContext {\n" +
                        "\n" +
                        "public Test ( ) {  \n" +
                        "name = \"Test\";\n" +
                        "}\n" +
                        "\n" +
                        "@Override\n" +
                        "public SOSPredicate predicate() {\n" +
                        "\n" +
                        "    SOSAgent agent = SOSAgent.instance();\n" +
                        "\n" +
                        "    return new SOSPredicateImpl(p -> {\n" +
                        "        try {\n" +
                        "            \n" +
                        "        } catch (Exception e) {\n" +
                        "            SOS_LOG.log(LEVEL.ERROR, \"Predicate could not be apply\");\n" +
                        "        }\n" +
                        "\n" +
                        "        return false;\n" +
                        "    });\n" +
                        "}\n" +
                        "\n" +
                        "@Override\n" +
                        "public Policy[] policies() {\n" +
                        "        return new Policy[]{  };\n" +
                        "}\n" +
                        "\n" +
                        "}\n";

        assertEquals(clazzString, MATCHING_CLAZZ);

    }

}