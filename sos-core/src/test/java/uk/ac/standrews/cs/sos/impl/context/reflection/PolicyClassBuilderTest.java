package uk.ac.standrews.cs.sos.impl.context.reflection;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicyClassBuilderTest {

    @Test
    public void basicClassConstruction() throws IOException {

        String JSON_POLICY =
                "{\n" +
                        "  \"Type\": \"Policy\",\n" +
                        "  \"Apply\": \"\",\n" +
                        "  \"Satisfied\": \"return true;\",\n" +
                        "  \"Dependencies\": [],\n" +
                        "  \"Fields\": [{\n" +
                        "    \"Type\": \"int\",\n" +
                        "    \"Name\": \"factor\",\n" +
                        "    \"Value\": \"2\"\n" +
                        "  }]\n" +
                        "}";

        System.out.println(JSON_POLICY);
        JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(JSON_POLICY);
        String clazzString = new PolicyClassBuilder().constructClass(jsonNode);

        String MATCHING_CLAZZ =
                "package uk.ac.standrews.cs.sos.impl.context;\n" +
                        "\n" +
                        "import uk.ac.standrews.cs.guid.IGUID;\n" +
                        "import uk.ac.standrews.cs.logger.LEVEL;\n" +
                        "import uk.ac.standrews.cs.sos.utils.SOS_LOG;\n" +
                        "import uk.ac.standrews.cs.sos.impl.services.SOSAgent;\n" +
                        "import uk.ac.standrews.cs.sos.impl.context.BasePolicy;\n" +
                        "import uk.ac.standrews.cs.sos.impl.context.PolicyActions;\n" +
                        "import uk.ac.standrews.cs.sos.interfaces.node.NodeType;\n" +
                        "import uk.ac.standrews.cs.sos.model.*;\n" +
                        "import uk.ac.standrews.cs.castore.data.Data;\n" +
                        "import uk.ac.standrews.cs.guid.GUIDFactory;\n" +
                        "import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;\n" +
                        "import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;\n" +
                        "import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;\n" +
                        "import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;\n" +
                        "import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;\n" +
                        "import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;\n" +
                        "import java.util.Collections;\n" +
                        "import java.util.Arrays;\n" +
                        "import java.util.Set;\n" +
                        "import com.fasterxml.jackson.databind.JsonNode;\n" +
                        "import java.io.IOException;\n" +
                        "\n" +
                        "public class SHA256_16_bfb31cfd5fbfd1bdf7e85cd4f12d557bcd21afb9f8bfd95b877bb4674a4d6c8d extends BasePolicy {\n" +
                        "\n" +
                        "private int factor = 2;\n" +
                        "\n" +
                        "public SHA256_16_bfb31cfd5fbfd1bdf7e85cd4f12d557bcd21afb9f8bfd95b877bb4674a4d6c8d (JsonNode policyManifest) {  \n" +
                        "super(policyManifest);\n" +
                        "}\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void apply(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException {\n" +
                        "    \n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public boolean satisfied(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException {\n" +
                        "    return true;\n" +
                        "    }\n" +
                        "\n" +
                        "}\n";

        assertEquals(clazzString, MATCHING_CLAZZ);
    }

}