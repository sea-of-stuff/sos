package uk.ac.standrews.cs.sos;

import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.filters.StorageFilter;
import uk.ac.standrews.cs.sos.json.JacksonProvider;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTConfig {

    private final static String REST_PACKAGE = "uk.ac.standrews.cs.sos.rest";

    public ResourceConfig build() {
        return new ResourceConfig()
                .packages(REST_PACKAGE)
                .register(JacksonProvider.class)
                .register(StorageFilter.class);
    }
}