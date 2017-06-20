package uk.ac.standrews.cs.sos.impl.context.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * This utility class provides the components needed to construct a properly working Java class for a context.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextClassBuilder {

    private static final String NEW_LINE = "\n";

    static final String PACKAGE = "uk.ac.standrews.cs.sos.impl.context";
    private static final String PACKAGE_DECLARATION = "package " + PACKAGE + ";" + NEW_LINE;
    private static final String IMPORTEE_TAG = "_IMPORTEE_";
    private static final String IMPORT = "import " + IMPORTEE_TAG + ";" + NEW_LINE;
    private static final String CLASS_NAME_TAG = "_CLASS_NAME_";
    private static final String COMMON_CLASS = "BaseContext";
    private static final String CLASS_SIGNATURE_TEMPLATE = "public class " + CLASS_NAME_TAG + " extends " + COMMON_CLASS + " {" + NEW_LINE;
    private static final String CLASS_CLOSING = "}";

    private static final String VAR_NAME = "_VAR_NAME_";
    private static final String VAL = "_VAL_";
    private static final String PRIVATE_STRING_VAR = VAR_NAME + " = \"" + VAL + "\";";

    private static final String CONSTRUCTOR_BODY = "super(policyActions, name, domain, codomain);";
    private static final String CONSTRUCTOR = "public " + CLASS_NAME_TAG + " (PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {  " + NEW_LINE + CONSTRUCTOR_BODY + NEW_LINE + "}" + NEW_LINE;

    private static final String CONSTRUCTOR_BODY_1 = "super(policyActions, guid, name, domain, codomain);";
    private static final String CONSTRUCTOR_1 = "public " + CLASS_NAME_TAG + " (PolicyActions policyActions, IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {  " + NEW_LINE + CONSTRUCTOR_BODY_1 + NEW_LINE + "}" + NEW_LINE;


    private static final String POLICIES_TAG = "_POLICIES_";

    private static final String POLICIES_METHODS =
            "@Override\n" +
            "public Policy[] policies() {\n" +
            "        return new Policy[]{ " + POLICIES_TAG + " };" + NEW_LINE +
            "}" + NEW_LINE;

    private static final String PREDICATE_TAG = "_PREDICATE_TAG_";
    private static final String PREDICATE_METHOD =
            "@Override\n" +
            "public SOSPredicate predicate() {\n" +
            "\n" +
            "    SOSAgent agent = SOSAgent.instance();\n" +
            "\n" +
            "    return new SOSPredicateImpl(" + PREDICATE_TAG + ", PREDICATE_ALWAYS_TRUE);\n" +
            "}" + NEW_LINE;


    private static final String JSON_NAME = "name";
    private static final String JSON_DEPENDENCIES = "dependencies";
    private static final String JSON_PREDICATE = "predicate";
    private static final String JSON_POLICIES = "policies";

    public static String ConstructClass(JsonNode node) throws IOException {

        String className = node.get(JSON_NAME).textValue();

        /////////////////////////
        // Package and Imports //
        /////////////////////////
        StringBuilder clazz = new StringBuilder(PACKAGE_DECLARATION);
        clazz.append(NEW_LINE);

        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.IGUID"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.LEVEL"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.impl.*"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.impl.actors.SOSAgent"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.NodesCollection"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.Policy"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.SOSPredicate"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.utils.SOS_LOG"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "java.util.Collections"));

        clazz.append(IMPORT.replace(IMPORTEE_TAG, "java.util.List"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "java.util.function.Predicate"));

        if (node.has(JSON_DEPENDENCIES)) {
            JsonNode dependencies = node.get(JSON_DEPENDENCIES);
            for (JsonNode dependency : dependencies) {
                clazz.append(IMPORT.replace(IMPORTEE_TAG, dependency.asText()));
            }
        }

        clazz.append(NEW_LINE);

        //////////////////////////
        // Class & Constructors //
        //////////////////////////
        clazz.append(CLASS_SIGNATURE_TEMPLATE.replace(CLASS_NAME_TAG, className));
        clazz.append(NEW_LINE);

        clazz.append(CONSTRUCTOR.replace(CLASS_NAME_TAG, className));
        clazz.append(NEW_LINE);

        clazz.append(CONSTRUCTOR_1.replace(CLASS_NAME_TAG, className));
        clazz.append(NEW_LINE);

        ///////////////
        // Predicate //
        ///////////////
        String predicate = node.has(JSON_PREDICATE) ? node.get(JSON_PREDICATE).asText() : "";
        clazz.append(PREDICATE_METHOD.replace(PREDICATE_TAG, predicate));
        clazz.append(NEW_LINE);

        //////////////
        // Policies //
        //////////////
        String policies = node.has(JSON_POLICIES) ? node.get(JSON_POLICIES).asText() : "";
        clazz.append(POLICIES_METHODS.replace(POLICIES_TAG, policies));
        clazz.append(NEW_LINE);

        clazz.append(CLASS_CLOSING);
        clazz.append(NEW_LINE);

        return clazz.toString();
    }
}
