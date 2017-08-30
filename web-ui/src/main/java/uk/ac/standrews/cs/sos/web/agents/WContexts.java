package uk.ac.standrews.cs.sos.web.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import spark.Request;
import spark.Response;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextContent;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextClassBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.web.VelocityUtils;
import uk.ac.standrews.cs.utilities.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WContexts {

    public static String Render(SOSLocalNode sos){
        Map<String, Object> model = new HashMap<>();
        model.put("contexts", sos.getCMS().getContexts());
        model.put("roles", sos.getRMS().getRoles());

        return VelocityUtils.RenderTemplate("velocity/contexts.vm", model);
    }

    public static String GetContents(Request req, SOSLocalNode sos) throws GUIDGenerationException, JsonProcessingException, ContextNotFoundException {
        String guidParam = req.params("id");
        IGUID contextGUID = GUIDFactory.recreateGUID(guidParam);

        Map<String, Object> model = new HashMap<>();

        Map<String, Object> contents = new HashMap<>();
        for(IGUID guid : sos.getCMS().getContents(contextGUID)) {
            ContextContent info = sos.getCMS().getContextContentInfo(contextGUID, guid);
            contents.put(guid.toMultiHash(), info);
        }

        model.put("contents", contents);

        Context context = sos.getCMS().getContext(contextGUID);
        model.put("context_json", context.toString());

        return JSONHelper.JsonObjMapper().writeValueAsString(model);
    }

    public static String CreateContext(Request request, Response response, SOSLocalNode sos) {

        try {
            String contextJSON = request.queryParams("contextJSON");
            sos.getCMS().addContext(contextJSON);

            response.redirect("/contexts");
            return "";

        } catch (Exception e) {

            response.redirect("/contexts");
            return "ERROR";
        }

    }

    public static String SearchContext(Request request, SOSLocalNode sos) {

        String nameToSearch = request.params("name");
        try {

            ArrayNode arrayNode = JSONHelper.JsonObjMapper().createArrayNode();
            Set<Context> contexts = sos.getCMS().searchContexts(nameToSearch);
            for(Context context:contexts) {
                arrayNode.add(context.toString());
            }

            return arrayNode.toString();

        } catch (ContextNotFoundException e) {

            return "ERROR";
        }

    }

    public static String PreviewClassContext(Request request, Response response) {

        try {
            String contextJSON = request.body();
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(contextJSON);
            String clazz = ContextClassBuilder.ConstructClass(jsonNode);

            response.status(200);
            return clazz;

        } catch (Exception e) {

            response.status(400);
            return "Unable to preview class";
        }

    }

    public static String Threads(SOSLocalNode sos) {

        ObjectNode node = JSONHelper.JsonObjMapper().createObjectNode();

        // Columns
        ArrayNode cols = node.putArray("cols");
        cols.add(
                JSONHelper.JsonObjMapper().createObjectNode()
                        .put("id", "Thread")
                        .put("label", "Thread")
                        .put("type", "string")
        );

        cols.add(
                JSONHelper.JsonObjMapper().createObjectNode()
                        .put("id", "Start")
                        .put("label", "Start")
                        .put("type", "date")
        );

        cols.add(
                JSONHelper.JsonObjMapper().createObjectNode()
                        .put("id", "End")
                        .put("label", "End")
                        .put("type", "date")
        );

        // Rows
        ArrayNode rows = node.putArray("rows");
        addThreadStats(rows, sos.getCMS().getPredicateThreadSessionStatistics(), "Predicate");
        addThreadStats(rows, sos.getCMS().getApplyPolicyThreadSessionStatistics(), "Policy (apply)");
        addThreadStats(rows, sos.getCMS().getCheckPolicyThreadSessionStatistics(), "Policy (check)");

        return node.toString();
    }

    private static void addThreadStats(ArrayNode rows, Queue<Pair<Long, Long>> stats, String label) {

        for (Pair<Long, Long> ts : stats) {
            ObjectNode cells = JSONHelper.JsonObjMapper().createObjectNode();
            ArrayNode values = cells.putArray("c");
            values.add(JSONHelper.JsonObjMapper().createObjectNode()
                    .put("v", label)
            )
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "Date(" + ts.X() + ")")
                    )
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "Date(" + ts.Y() + ")") // Adding half a second, otherwise data cannot be visualised
                    );

            rows.add(cells);

        }
    }

    public static String RunPredicate(Request req, SOSLocalNode sos) throws GUIDGenerationException, JsonProcessingException, ContextNotFoundException {
        String guidParam = req.params("id");
        IGUID contextGUID = GUIDFactory.recreateGUID(guidParam);

        sos.getCMS().runContextPredicateNow(contextGUID);
        return "";
    }

    public static String RunPolicies(Request req, SOSLocalNode sos) throws GUIDGenerationException, JsonProcessingException, ContextNotFoundException {
        String guidParam = req.params("id");
        IGUID contextGUID = GUIDFactory.recreateGUID(guidParam);

        sos.getCMS().runContextPolicyNow(contextGUID);
        return "";
    }

    public static String RunCheckPolicies(Request req, SOSLocalNode sos) throws GUIDGenerationException, JsonProcessingException, ContextNotFoundException {
        String guidParam = req.params("id");
        IGUID contextGUID = GUIDFactory.recreateGUID(guidParam);

        sos.getCMS().runContextPolicyCheckNow(contextGUID);
        return "";
    }

}
