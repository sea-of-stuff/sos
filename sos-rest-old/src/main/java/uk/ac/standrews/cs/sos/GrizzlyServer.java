package uk.ac.standrews.cs.sos;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;


/**
 * @author Simone I. Conte "sic2@standrews.ac.uk"
 */
public class GrizzlyServer {

    // Base URI the Grizzly HTTP server will listen on
    public static final URI BASE_URI = UriBuilder.fromUri("http://0.0.0.0/sos/")
            .port(8080)
            .build();

    public static HttpServer startServer() throws IOException {
        ServerState.startSOS();
        final ResourceConfig rc = new ResourceConfig()
                .packages("uk.ac.standrews.cs.sos.rest")
                .register(LoggingFilter.class);
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI.toString()));
        System.in.read();
        server.shutdownNow();
    }

}
