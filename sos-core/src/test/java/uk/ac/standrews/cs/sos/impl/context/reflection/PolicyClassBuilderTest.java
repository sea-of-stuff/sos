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
                        "public class SHA256_16_e9326960467fd93dbe3518f2009f4106e30f724f4f515ffc71e038cbe2577791 extends BasePolicy {\n" +
                        "\n" +
                        "public SHA256_16_e9326960467fd93dbe3518f2009f4106e30f724f4f515ffc71e038cbe2577791 (PolicyActions policyActions, String policyManifest) {  \n" +
                        "super(policyActions, policyManifest);\n" +
                        "}\n" +
                        "\n" +
                        "@Override\n" +
                        "    public void apply(NodesCollection codomain, Manifest manifest) throws PolicyException {\n" +
                        "    true\n" +
                        "    }@Override\n" +
                        "    public boolean satisfied(NodesCollection codomain, Manifest manifest) throws PolicyException {\n" +
                        "    true\n" +
                        "    }}\n";

        assertEquals(clazzString, MATCHING_CLAZZ);
    }

}