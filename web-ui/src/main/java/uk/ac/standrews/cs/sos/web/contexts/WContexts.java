package uk.ac.standrews.cs.sos.web.contexts;

import spark.Request;
import spark.Response;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextLoader;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Context;
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

    public static String CreateContext(Request request, Response response, SOSLocalNode sos) {


        try {
            String contextJSON = request.queryParams("contextJSON");
            ContextLoader.LoadContext(contextJSON);

            Context context = ContextLoader.Instance("Test");
            sos.getCMS().addContext(context);

            response.redirect("/contexts");
            return "";

        } catch (Exception e) {

            response.redirect("/contexts");
            return "ERROR";
        }

    }

}
