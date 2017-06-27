package uk.ac.standrews.cs.sos.web.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import spark.Request;
import spark.Response;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextClassBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WContexts {

    public static String Render(SOSLocalNode sos){
        Map<String, Object> model = new HashMap<>();
        model.put("contexts", sos.getCMS().getContexts());

        return VelocityUtils.RenderTemplate("velocity/contexts.vm", model);
    }

    public static String GetContents(Request req, SOSLocalNode sos) throws GUIDGenerationException {
        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);

        Map<String, Object> model = new HashMap<>();
        model.put("contents", sos.getCMS().getContents(guid));

        // TODO - test this
        return VelocityUtils.RenderTemplate("velocity/contexts.vm", model);
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

    public static String PreviewClassContext(Request request, Response response) {

        try {
            String contextJSON = request.body();
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(contextJSON);
            String clazz = ContextClassBuilder.ConstructClass(jsonNode);

            response.status(200);
            return clazz;

        } catch (Exception e) {

            // TODO - this is not displayed :/
            response.status(400);
            return "Unable to preview class";
        }

    }

}
