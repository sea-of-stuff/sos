package uk.ac.standrews.cs.sos;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.filters.StorageFilter;
import uk.ac.standrews.cs.sos.json.JacksonProvider;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTConfig {

    public ResourceConfig build() {
        return new ResourceConfig()
                .packages("uk.ac.standrews.cs.sos.rest")
                .register(JacksonProvider.class)
                .register(StorageFilter.class)
                .register(LoggingFilter.class);
    }
}