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
public class PredicateClassBuilderTest {

    @Test
    public void basicClassConstruction() throws IOException {

        String JSON_PREDICATE =
                "{\n" +
                        "\t\"type\": \"Predicate\",\n" +
                        "\t\"predicate\": \"true;\"\n" +
                        "}";

        JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(JSON_PREDICATE);
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
                        "public class SHA256_16_bb568e69b0b8b04afc620a4c7727b2f7d9e029bd02de8c1284268e9a03b68d6e extends BasePredicate {\n" +
                        "\n" +
                        "public SHA256_16_bb568e69b0b8b04afc620a4c7727b2f7d9e029bd02de8c1284268e9a03b68d6e (JsonNode predicateManifest) throws Exception {  \n" +
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
                        "\t\"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Arrays.asList(\\\"text/plain; charset=UTF-8\\\", \\\"text/plain; charset=windows-1252\\\", \\\"text/plain; charset=ISO-8859-1\\\")) && CommonPredicates.SearchText(guid, \\\"the\\\");\"\n" +
                        "}";

        JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(JSON_PREDICATE);
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
                        "public class SHA256_16_2f6b76c33117a63137fae7b9803235c149c7d883794a201a53dd61e7461db683 extends BasePredicate {\n" +
                        "\n" +
                        "public SHA256_16_2f6b76c33117a63137fae7b9803235c149c7d883794a201a53dd61e7461db683 (JsonNode predicateManifest) throws Exception {  \n" +
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
