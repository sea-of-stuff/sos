package uk.ac.standrews.cs.sos.web.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WWebArchive {

    public static String Render() throws JsonProcessingException {
        return VelocityUtils.RenderTemplate("velocity/webarchive.vm");
    }
}
