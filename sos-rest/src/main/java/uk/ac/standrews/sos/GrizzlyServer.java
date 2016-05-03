package uk.ac.standrews.sos;

import com.google.gson.GsonBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.deserializers.ContentDeserializer;
import uk.ac.standrews.cs.sos.deserializers.LocationBundleDeserializer;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.Content;

import java.io.IOException;
import java.net.URI;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GrizzlyServer {

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:8080/sos/";

    public static HttpServer startServer() throws IOException {
        ServerState.startSOS();
        configureGson();

        final ResourceConfig rc = new ResourceConfig()
                .packages("uk.ac.standrews.cs.rest")
                .register(LoggingFilter.class);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }



    private static void configureGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        registerGSonTypeAdapters(gsonBuilder);
        ServerState.gson = gsonBuilder.create();
    }

    private static void registerGSonTypeAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(LocationBundle.class, new LocationBundleDeserializer());
        builder.registerTypeAdapter(Content.class, new ContentDeserializer());
    }
}
