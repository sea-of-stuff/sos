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
                        "\t\"type\": \"Predicate\",\n" +
                        "\t\"predicate\": \"true;\",\n" +
                        "\t\"dependencies\": []\n" +
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
                "public class SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e extends BasePredicate {\n" +
                "\n" +
                "public SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e (JsonNode predicateManifest) throws Exception {  \n" +
                "super(predicateManifest);\n" +
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

    @Test
    public void classConstructionWithArrayList() throws IOException {

        String JSON_PREDICATE =
                "{\n" +
                        "\t\"type\": \"Predicate\",\n" +
                        "\t\"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Arrays.asList(\\\"text/plain; charset=UTF-8\\\", \\\"text/plain; charset=windows-1252\\\", \\\"text/plain; charset=ISO-8859-1\\\")) && CommonPredicates.SearchText(guid, \\\"the\\\");\",\n" +
                        "\t\"dependencies\": []\n" +
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
                        "public class SHA256_16_76c13dd6c14d8c47c40303c18f8f189ba701855a05e5a396390448806fb55e52 extends BasePredicate {\n" +
                        "\n" +
                        "public SHA256_16_76c13dd6c14d8c47c40303c18f8f189ba701855a05e5a396390448806fb55e52 (JsonNode predicateManifest) throws Exception {  \n" +
                        "super(predicateManifest);\n" +
                        "}\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public boolean test(IGUID guid) {\n" +
                        "\n" +
                        "        return CommonPredicates.ContentTypePredicate(guid, Arrays.asList(\"text/plain; charset=UTF-8\", \"text/plain; charset=windows-1252\", \"text/plain; charset=ISO-8859-1\")) && CommonPredicates.SearchText(guid, \"the\");\n" +
                        "    }\n" +
                        "}\n";

        assertEquals(clazzString, MATCHING_CLAZZ);
    }
}
