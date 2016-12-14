package uk.ac.standrews.cs.sos;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.filters.DDSFilter;
import uk.ac.standrews.cs.sos.filters.NDSFilter;
import uk.ac.standrews.cs.sos.filters.StorageFilter;
import uk.ac.standrews.cs.sos.json.JacksonProvider;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTConfig extends ResourceConfig {

    private final static String REST_PACKAGE = "uk.ac.standrews.cs.sos.rest";

    public static SOSLocalNode sos;

    public RESTConfig() {
        packages(REST_PACKAGE);
        register(LoggingFeature.class);
        register(JacksonProvider.class);
        register(JacksonFeature.class);

        register(StorageFilter.class);
        register(NDSFilter.class);
        register(DDSFilter.class);
    }

    public void setSOS(SOSLocalNode sos) {
        RESTConfig.sos = sos;
    }
}
