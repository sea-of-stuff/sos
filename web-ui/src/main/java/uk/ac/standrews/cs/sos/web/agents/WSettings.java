package uk.ac.standrews.cs.sos.web.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WSettings {

    public static String Render(SOSLocalNode sos) throws JsonProcessingException {
        Map<String, Object> model = new HashMap<>();

        model.put("settings", JSONHelper.JsonObjMapper().writeValueAsString(SOSLocalNode.settings));

        return VelocityUtils.RenderTemplate("velocity/settings.vm", model);
    }
}

