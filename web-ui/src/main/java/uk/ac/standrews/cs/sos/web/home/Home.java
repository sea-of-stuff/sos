package uk.ac.standrews.cs.sos.web.home;

import uk.ac.standrews.cs.sos.web.VelocityUtils;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Home {

    public static String Render() {
        return VelocityUtils.RenderTemplate("velocity/index.vm");
    }

}
