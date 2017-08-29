package uk.ac.standrews.cs.sos.impl.context.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.text.WordUtils;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

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

    private static final String CONSTRUCTOR_BODY = "super(jsonNode, policyActions, name, domain, codomain);";
    private static final String CONSTRUCTOR = "public " + CLASS_NAME_TAG + " (JsonNode jsonNode, PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain) {  " + NEW_LINE + CONSTRUCTOR_BODY + NEW_LINE + "}" + NEW_LINE;

    private static final String CONSTRUCTOR_BODY_1 = "super(jsonNode, policyActions, guid, name, domain, codomain);";
    private static final String CONSTRUCTOR_1 = "public " + CLASS_NAME_TAG + " (JsonNode jsonNode, PolicyActions policyActions, IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {  " + NEW_LINE + CONSTRUCTOR_BODY_1 + NEW_LINE + "}" + NEW_LINE;


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
            "    return new P(PREDICATE_ALWAYS_TRUE);\n" +
            "}" + NEW_LINE;

    private static final String INNER_PREDICATE_CLASS = "" +
            "class P extends SOSPredicateImpl {\n" +
            "\n" +
            "    P(long maxAge) {\n" +
            "        super(maxAge);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public boolean test(IGUID guid) {\n" +
            "        return " + PREDICATE_TAG + "\n" + // FIXME - the return here is limiting
            "    }\n" +
            "}";

    public static final String CONTEXT_JSON_NAME = "name";
    private static final String CONTEXT_JSON_DEPENDENCIES = "dependencies";
    private static final String CONTEXT_JSON_PREDICATE = "predicate";
    private static final String CONTEXT_JSON_POLICIES = "policies";
    public static final String CONTEXT_JSON_DOMAIN = "domain";
    public static final String CONTEXT_JSON_CODOMAIN = "codomain";

    public static String ConstructClass(JsonNode node) throws IOException {

        String className = WordUtils.capitalize(node.get(CONTEXT_JSON_NAME).textValue());

        /////////////////////////
        // Package and Imports //
        /////////////////////////
        StringBuilder clazz = new StringBuilder(PACKAGE_DECLARATION);
        clazz.append(NEW_LINE);

        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.guid.IGUID"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.logger.LEVEL"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.impl.services.SOSAgent"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.NodesCollection"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.Policy"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.SOSPredicate"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.utils.SOS_LOG"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "java.util.Collections"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "java.util.Arrays"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "com.fasterxml.jackson.databind.JsonNode"));

        if (node.has(CONTEXT_JSON_DEPENDENCIES)) {
            JsonNode dependencies = node.get(CONTEXT_JSON_DEPENDENCIES);
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
        String predicate = node.has(CONTEXT_JSON_PREDICATE) ? node.get(CONTEXT_JSON_PREDICATE).asText() : "";
        clazz.append(PREDICATE_METHOD);
        clazz.append(NEW_LINE);
        clazz.append(INNER_PREDICATE_CLASS.replace(PREDICATE_TAG, predicate));

        //////////////
        // Policies //
        //////////////
        String policies_rpl = "";
        if (node.has(CONTEXT_JSON_POLICIES)) {
            JsonNode policies = node.get(CONTEXT_JSON_POLICIES);
            for (JsonNode policy : policies) {
                policies_rpl += "new " + policy.asText() + ",";
            }
            policies_rpl = policies_rpl.substring(0, policies_rpl.length() - 1); // remove last comma
        }

        clazz.append(POLICIES_METHODS.replace(POLICIES_TAG, policies_rpl));
        clazz.append(NEW_LINE);

        clazz.append(CLASS_CLOSING);
        clazz.append(NEW_LINE);

        return clazz.toString();
    }

    public static NodesCollection makeNodesCollection(JsonNode jsonNode, String tag) throws GUIDGenerationException, NodeNotFoundException, NodesCollectionException {
        NodesCollection retval;
        NodesCollection.TYPE type = NodesCollection.TYPE.LOCAL;
        Set<IGUID> nodes = new LinkedHashSet<>();
        if (jsonNode.has(tag)) {
            type = NodesCollection.TYPE.valueOf(jsonNode.get(tag).get("type").asText());
            JsonNode nodeRefs = jsonNode.get(tag).get("nodes");

            for(JsonNode nodeRef:nodeRefs) {
                IGUID ref = GUIDFactory.recreateGUID(nodeRef.asText());
                nodes.add(ref);
            }

            retval = new NodesCollectionImpl(type, nodes);
        } else {
            retval = new NodesCollectionImpl(type);
        }

        return retval;
    }
}
