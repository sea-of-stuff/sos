package uk.ac.standrews.cs.sos;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@ApplicationPath("rest")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("uk.ac.standrews.cs.sos.rest");
    }
}
