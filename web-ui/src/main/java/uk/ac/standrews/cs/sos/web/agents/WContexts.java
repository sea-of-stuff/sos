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
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextVersionInfo;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.web.VelocityUtils;
import uk.ac.standrews.cs.utilities.Pair;

import java.util.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WContexts {

    public static String Render(SOSLocalNode sos){
        Map<String, Object> model = new HashMap<>();

        ContextService contextService = sos.getCMS();

        Set<Context> contexts = new LinkedHashSet<>();
        for(IGUID contextRef:contextService.getContexts()) {

            try {
                Context context = contextService.getContext(contextRef);
                contexts.add(context);
            } catch (ContextNotFoundException e) {
                /* do nothing */
            }
        }
        model.put("contexts", contexts);

        UsersRolesService usersRolesService = sos.getUSRO();

        Set<Pair<User, Role>> usro = new LinkedHashSet<>();
        for(IGUID roleRef:usersRolesService.getRoles()) {
            try {
                Role role = usersRolesService.getRole(roleRef);
                User user = usersRolesService.getUser(role.getUser());
                usro.add(new Pair<>(user, role));
            } catch (RoleNotFoundException | UserNotFoundException e) { /* do nothing */ }
        }
        model.put("usro", usro);


        return VelocityUtils.RenderTemplate("velocity/contexts.vm", model);
    }

    public static String GetContents(Request req, SOSLocalNode sos) throws GUIDGenerationException, JsonProcessingException, ContextNotFoundException {

        try {
            ContextService contextService = sos.getCMS();

            String guidParam = req.params("id");
            IGUID contextGUID = GUIDFactory.recreateGUID(guidParam);
            Context context = contextService.getContext(contextGUID);
            Context tipContext = contextService.getContextTIP(context.invariant());
            Map<String, Object> model = new HashMap<>();

            Map<String, Object> contents = new HashMap<>();
            for (IGUID guid : contextService.getContents(tipContext.guid())) {
                ContextVersionInfo info = contextService.getContextContentInfo(context.invariant(), guid);
                contents.put(guid.toMultiHash(), info);
            }

            model.put("contents", contents);
            model.put("context_json", tipContext.toString());

            return JSONHelper.jsonObjMapper().writeValueAsString(model);

        } catch (TIPNotFoundException e) {
            return "Unable to find context tip";
        }
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

        ContextService contextService = sos.getCMS();

        String nameToSearch = request.params("name");
        try {
            ArrayNode arrayNode = JSONHelper.jsonObjMapper().createArrayNode();
            Set<IGUID> contexts = contextService.searchContexts(nameToSearch);
            for(IGUID contextRef:contexts) {
                Context context = contextService.getContext(contextRef);
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
            JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(contextJSON);
            String clazz = "N/A - have to use predicate and policy class builders"; // ContextClassBuilder.ConstructClass(jsonNode);

            response.status(200);
            return clazz;

        } catch (Exception e) {

            response.status(400);
            return "Unable to preview class";
        }

    }

    public static String Threads(SOSLocalNode sos) {

        ObjectNode node = JSONHelper.jsonObjMapper().createObjectNode();

        // Columns
        ArrayNode cols = node.putArray("cols");
        cols.add(
                JSONHelper.jsonObjMapper().createObjectNode()
                        .put("id", "Thread")
                        .put("label", "Thread")
                        .put("type", "string")
        );

        cols.add(
                JSONHelper.jsonObjMapper().createObjectNode()
                        .put("id", "Start")
                        .put("label", "Start")
                        .put("type", "date")
        );

        cols.add(
                JSONHelper.jsonObjMapper().createObjectNode()
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
            ObjectNode cells = JSONHelper.jsonObjMapper().createObjectNode();
            ArrayNode values = cells.putArray("c");
            values.add(JSONHelper.jsonObjMapper().createObjectNode()
                    .put("v", label)
            )
                    .add(JSONHelper.jsonObjMapper().createObjectNode()
                            .put("v", "Date(" + ts.X() + ")")
                    )
                    .add(JSONHelper.jsonObjMapper().createObjectNode()
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
