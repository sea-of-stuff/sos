/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
                        "  \"type\": \"Policy\",\n" +
                        "  \"apply\": \"\",\n" +
                        "  \"satisfied\": \"return true;\",\n" +
                        "  \"fields\": [{\n" +
                        "    \"type\": \"int\",\n" +
                        "    \"name\": \"factor\",\n" +
                        "    \"value\": \"2\"\n" +
                        "  }]\n" +
                        "}";

        System.out.println(JSON_POLICY);
        JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(JSON_POLICY);
        String clazzString = new PolicyClassBuilder().constructClass(jsonNode);

        String MATCHING_CLAZZ =
                "package uk.ac.standrews.cs.sos.impl.context;\n" +
                        "\n" +
                        "import uk.ac.standrews.cs.guid.IGUID;\n" +
                        "import uk.ac.standrews.cs.logger.LEVEL;\n" +
                        "import uk.ac.standrews.cs.sos.utils.SOS_LOG;\n" +
                        "import uk.ac.standrews.cs.sos.impl.services.SOSAgent;\n" +
                        "import uk.ac.standrews.cs.sos.impl.context.BasePolicy;\n" +
                        "import uk.ac.standrews.cs.sos.impl.context.CommonUtilities;\n" +
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
                        "public class SHA256_16_8910463e9de02413720c413e6dcf569cc4de73b32f03bc88571f455199558844 extends BasePolicy {\n" +
                        "\n" +
                        "private int factor = 2;\n" +
                        "\n" +
                        "public SHA256_16_8910463e9de02413720c413e6dcf569cc4de73b32f03bc88571f455199558844 (JsonNode policyManifest) throws Exception {  \n" +
                        "super(policyManifest);\n" +
                        "}\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void apply(NodesCollection codomain, CommonUtilities utilities, Manifest manifest) throws PolicyException {\n" +
                        "    \n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public boolean satisfied(NodesCollection codomain, CommonUtilities utilities, Manifest manifest) throws PolicyException {\n" +
                        "    return true;\n" +
                        "    }\n" +
                        "\n" +
                        "}\n";

        assertEquals(clazzString, MATCHING_CLAZZ);
    }

    @Test
    public void dataReplicationClass() throws IOException {

        String JSON_POLICY =
                "{\n" +
                        "  \"type\" : \"Policy\",\n" +
                        "  \"apply\" : \"CommonPolicies.replicateData(codomain, utilities, manifest, factor);\",\n" +
                        "  \"satisfied\" : \"return CommonPolicies.dataIsReplicated(codomain, utilities, manifest, factor);\",\n" +
                        "  \"fields\" : [ {\n" +
                        "    \"type\" : \"int\",\n" +
                        "    \"name\" : \"factor\",\n" +
                        "    \"value\" : \"1\"\n" +
                        "  } ]\n" +
                        "}";

        System.out.println(JSON_POLICY);
        JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(JSON_POLICY);
        String clazzString = new PolicyClassBuilder().constructClass(jsonNode);

        System.out.println(clazzString);

    }
}