package uk.ac.standrews.cs.sos.web.contexts;

import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WContexts {

    public static String Render(SOSLocalNode sos){

        return VelocityUtils.RenderTemplate("velocity/contexts.vm");
    }
}
