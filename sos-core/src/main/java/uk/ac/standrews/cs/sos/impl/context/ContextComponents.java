package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

/**
 * This utility class provides the components needed to construct a properly working Java class for a context.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextComponents {

    public static final String NEW_LINE = "\n";

    public static final String PACKAGE = "package uk.ac.standrews.cs.sos.impl.context" + NEW_LINE;
    public static final String IMPORTEE_TAG = "_IMPORTEE_";
    public static final String IMPORT = "import " + IMPORTEE_TAG + NEW_LINE;
    public static final String CLASS_NAME_TAG = "_CLASS_NAME_";
    public static final String COMMON_CLASS = "CommonContext";
    public static final String CLASS_SIGNATURE_TEMPLATE = "public class " + CLASS_NAME_TAG + " extends " + COMMON_CLASS + " {" + NEW_LINE;

    public static final String CLASS_CLOSING = "}";
    public static final String CONSTRUCTOR = "public " + CLASS_NAME_TAG + " () {" + NEW_LINE;

    public static final String POLICIES_TAG = "_POLICIES_";

    public static final String POLICIES_METHODS =
            "@Override\n" +
            "public Policy[] policies() {\n" +
            "        return new Policy[]{ " + POLICIES_TAG + " }" +
            "}" + NEW_LINE;

    public static final String PREDICATE_TAG = "_PREDICATE_TAG_";
    public static final String PREDICATE_METHOD =
            "@Override\n" +
            "public SOSPredicate predicate() {\n" +
            "\n" +
            "    SOSAgent agent = SOSAgent.instance();\n" +
            "\n" +
            "    return new SOSPredicateImpl(p -> {\n" +
            "        try {\n" +
            "            " + PREDICATE_TAG +
            "\n" +
            "        } catch (Exception e) {\n" +
            "            SOS_LOG.log(LEVEL.ERROR, \"Predicate could not be run\");\n" +
            "        }\n" +
            "\n" +
            "        return false;\n" +
            "    });\n" +
            "}" + NEW_LINE;

    public static String ConstructClass(String jsonContext) throws IOException {

        // TODO - parse the json context and build the class
        JsonNode node = JSONHelper.JsonObjMapper().readTree(jsonContext);
        String className = node.get("NAME").textValue();
        JsonNode dependencies = node.get("dependencies");

        // TODO - if guid is known then set, otherwise generate it

        String clazz = PACKAGE;
        clazz += NEW_LINE;

        clazz += IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.*;");
        // iterate over imports
        for(JsonNode dependency:dependencies) {
            clazz += IMPORT.replace(IMPORTEE_TAG, dependency.asText());
        }
        clazz += NEW_LINE;

        clazz += CLASS_SIGNATURE_TEMPLATE.replace(CLASS_NAME_TAG, className);

        clazz += CONSTRUCTOR.replace(CLASS_NAME_TAG, className);
        clazz += NEW_LINE;

        // Predicate
        clazz += PREDICATE_METHOD; // TODO - replace

        // Policy
        clazz += POLICIES_METHODS; // TODO - replace


        clazz += CLASS_CLOSING;
        clazz += NEW_LINE;

        return clazz;
    }
}
