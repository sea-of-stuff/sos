package uk.ac.standrews.cs.sos.web.home;

import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WHome {

    public static String Render(SOSLocalNode sos) {
        // TODO - show node configuration and stats?

        Map<String, Object> model = new HashMap<>();
        model.put("assets",sos.getDDS().getAllVersions());

        return VelocityUtils.RenderTemplate("velocity/index.vm", model);
    }

}
