package uk.ac.standrews.cs.sos.grizzly;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.ServerState;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;


/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */

public class App {

    public static UriBuilder uriBuilder = UriBuilder.fromUri("http://0.0.0.0/");

    private static final int DEFAULT_SERVER_PORT = 8080;
    private static int serverPort;
    private static URI baseUri;

    public static HttpServer startServer() throws IOException {
        final ResourceConfig rc = new ResourceConfig()
                .packages("uk.ac.standrews.cs.sos.rest")
                .register(LoggingFilter.class);

        ServerState.init();

        baseUri = uriBuilder.port(serverPort).build();
        return GrizzlyHttpServerFactory.createHttpServer(baseUri, rc);
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        } else {
            serverPort = DEFAULT_SERVER_PORT;
        }

        final HttpServer server = startServer();

        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", baseUri.toString()));
        System.in.read();

        server.shutdownNow();
        ServerState.kill();
    }
}
