package uk.ac.standrews.cs.sos.impl.context.reflection;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PredicateClassBuilderTest {

    @Test
    public void basicClassConstruction() throws IOException {

        String JSON_PREDICATE =
                "{\n" +
                        "\t\"Type\": \"Predicate\",\n" +
                        "\t\"Predicate\": \"true;\",\n" +
                        "\t\"Dependencies\": []\n" +
                        "}";

        JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(JSON_PREDICATE);
        String clazzString = new PredicateClassBuilder().constructClass(jsonNode);

        String MATCHING_CLAZZ =
                "package uk.ac.standrews.cs.sos.impl.context;\n" +
                "\n" +
                "import uk.ac.standrews.cs.guid.IGUID;\n" +
                "import uk.ac.standrews.cs.logger.LEVEL;\n" +
                "import uk.ac.standrews.cs.sos.impl.services.SOSAgent;\n" +
                "import uk.ac.standrews.cs.sos.model.NodesCollection;\n" +
                "import uk.ac.standrews.cs.sos.model.Policy;\n" +
                "import uk.ac.standrews.cs.sos.utils.SOS_LOG;\n" +
                "import java.util.Collections;\n" +
                "import java.util.Arrays;\n" +
                "import com.fasterxml.jackson.databind.JsonNode;\n" +
                "\n" +
                "public class SHA256_16_3d3ee2b48a92d053a1089cbc837b9415dac064ba897d8699a14148778510ebc4 extends BasePredicate {\n" +
                "\n" +
                "public SHA256_16_3d3ee2b48a92d053a1089cbc837b9415dac064ba897d8699a14148778510ebc4 (String predicateManifest, long maxAge) {  \n" +
                "super(predicateManifest, maxAge);\n" +
                "}\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean test(IGUID guid) {\n" +
                "\n" +
                "        return true;\n" +
                "    }\n" +
                "}\n";

        assertEquals(clazzString, MATCHING_CLAZZ);
    }
}
