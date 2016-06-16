package uk.ac.standrews.cs.sos;


import uk.ac.standrews.cs.sos.rest.Roles;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@ApplicationPath("/rest/sos")
public class ApplicationConfig extends Application {

    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(Hello.class, Roles.class));
    }
}
