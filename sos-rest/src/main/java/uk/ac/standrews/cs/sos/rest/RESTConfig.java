package uk.ac.standrews.cs.sos.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.jaxrs.config.BeanConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.rest.filters.*;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTConfig extends ResourceConfig {

    private final static String SOS_API_VERSION = "1.0";
    private final static String REST_PACKAGE = "uk.ac.standrews.cs.sos.rest";

    public static SOSLocalNode sos;

    public RESTConfig() {

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(SOS_API_VERSION);
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setBasePath("/");
        beanConfig.setResourcePackage(REST_PACKAGE);
        beanConfig.setTitle("SOS API");
        beanConfig.setDescription("This REST API exposes the SOS to the outside world.");
        //beanConfig.setScan(true);
        beanConfig.setPrettyPrint(true);

        packages(REST_PACKAGE);

        register(LoggingFeature.class);
        register(JacksonProvider.class);
        register(JacksonFeature.class);

//        register(ApiListingResource.class);
//        register(SwaggerSerializers.class);

        register(StorageFilter.class);
        register(NDSFilter.class);
        register(DDSFilter.class);
        register(CMSFilter.class);
        register(RMSFilter.class);
        register(MMSFilter.class);

    }

    public RESTConfig setSOS(SOSLocalNode sos) {
        RESTConfig.sos = sos;
        return this;
    }

    @Provider
    public static class JacksonProvider implements ContextResolver<ObjectMapper> {

        private static final ObjectMapper MAPPER = new ObjectMapper();

        static {
            MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
            MAPPER.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
            MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        }

        public JacksonProvider() {
            System.out.println("Instantiate JacksonProvider");
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return MAPPER;
        }
    }
}
