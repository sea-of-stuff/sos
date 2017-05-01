package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * This utility class provides the components needed to construct a properly working Java class for a context.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextBuilder {

    public static final String NEW_LINE = "\n";

    public static final String PACKAGE = "uk.ac.standrews.cs.sos.impl.context";
    public static final String PACKAGE_DECLARATION = "package " + PACKAGE + ";" + NEW_LINE;
    public static final String IMPORTEE_TAG = "_IMPORTEE_";
    public static final String IMPORT = "import " + IMPORTEE_TAG + ";" + NEW_LINE;
    public static final String CLASS_NAME_TAG = "_CLASS_NAME_";
    public static final String COMMON_CLASS = "CommonContext";
    public static final String CLASS_SIGNATURE_TEMPLATE = "public class " + CLASS_NAME_TAG + " extends " + COMMON_CLASS + " {" + NEW_LINE;
    public static final String CLASS_CLOSING = "}";

    public static final String VAR_NAME = "_VAR_NAME_";
    public static final String VAL = "_VAL_";
    public static final String PRIVATE_STRING_VAR = VAR_NAME + " = \"" + VAL + "\";";

    public static final String CONSTRUCTOR_BODY = "_CONSTRUCTOR_BODY_";
    public static final String CONSTRUCTOR = "public " + CLASS_NAME_TAG + " ( ) {  " + NEW_LINE + CONSTRUCTOR_BODY + NEW_LINE + "}" + NEW_LINE;

    public static final String POLICIES_TAG = "_POLICIES_";

    public static final String POLICIES_METHODS =
            "@Override\n" +
            "public Policy[] policies() {\n" +
            "        return new Policy[]{ " + POLICIES_TAG + " };" + NEW_LINE +
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

    public static String ConstructClass(JsonNode node) throws IOException {

        String className = node.get("name").textValue();
        JsonNode dependencies = node.get("dependencies");

        // TODO - if guid is known then set, otherwise generate it

        StringBuilder clazz = new StringBuilder(PACKAGE_DECLARATION);
        clazz.append(NEW_LINE);

        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.*"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.SOSPredicate"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.LEVEL"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.impl.actors.SOSAgent"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.utils.SOS_LOG"));

        for(JsonNode dependency:dependencies) {
            clazz.append(IMPORT.replace(IMPORTEE_TAG, dependency.asText()));
        }
        clazz.append(NEW_LINE);

        clazz.append(CLASS_SIGNATURE_TEMPLATE.replace(CLASS_NAME_TAG, className));

        // TODO - refactor
        clazz.append(CONSTRUCTOR.replace(CLASS_NAME_TAG, className)
                                .replace(CONSTRUCTOR_BODY, PRIVATE_STRING_VAR
                                .replace(VAR_NAME, "name")
                                .replace(VAL, className)));
        clazz.append(NEW_LINE);

        // Predicate
        clazz.append(PREDICATE_METHOD.replace(PREDICATE_TAG, "System.out.println();")); // TODO - replace
        clazz.append(NEW_LINE);

        // Policy
        clazz.append(POLICIES_METHODS.replace(POLICIES_TAG, "")); // TODO - replace
        clazz.append(NEW_LINE);

        clazz.append(CLASS_CLOSING);
        clazz.append(NEW_LINE);

        return clazz.toString();
    }
}
