package uk.ac.standrews.cs.sos.web.agents;

import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WNodes {

    public static String Render(SOSLocalNode sos){
        Map<String, Object> model = new HashMap<>();

        model.put("thisNode", sos.getNDS().getThisNode());
        model.put("nodes", sos.getNDS().getAllNodes());

        return VelocityUtils.RenderTemplate("velocity/nodes.vm", model);
    }
}
