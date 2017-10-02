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

        String JSON_CONTEXT =
                "{\n" +
                        "\t\"Type\": \"Predicate\",\n" +
                        "\t\"Predicate\": \"true\",\n" +
                        "\t\"Dependencies\": []\n" +
                        "}";

        JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);
        String clazzString = new PredicateClassBuilder().constructClass(jsonNode);

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
                "public class SHA256_16_3cbc87c7681f34db4617feaa2c8801931bc5e42d8d0f560e756dd4cd92885f18 extends BasePredicate {\n" +
                "\n" +
                "public SHA256_16_3cbc87c7681f34db4617feaa2c8801931bc5e42d8d0f560e756dd4cd92885f18 (String predicate, long maxAge) {  \n" +
                "super(predicate, maxAge);\n" +
                "}\n" +
                "\n" +
                "@Override\n" +
                "    public boolean test(IGUID guid) {\n" +
                "\n" +
                "        return True;\n" +
                "    }\n" +
                "}\n";

        assertEquals(clazzString, MATCHING_CLAZZ);
    }
}
