package uk.ac.standrews.cs.sos.jetty;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.ServerState;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JettyApp {

    public static UriBuilder uriBuilder = UriBuilder.fromUri("http://0.0.0.0/");

    private static final int DEFAULT_SERVER_PORT = 9998;
    private static int serverPort;
    private static URI baseUri;

    public static Server startServer() throws Exception {
        final ResourceConfig rc = new RESTConfig().build();

        ServerState.init();
        baseUri = uriBuilder.port(serverPort).build();
        return JettyHttpContainerFactory.createServer(baseUri, rc);
    }

    /**
     * Start a SOS instance and expose it via a Jetty Server.
     *
     * The following parameters are allowed:
     * - port (default port is 9998)
     * - configuration file
     * Example:
     *
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        } else {
            serverPort = DEFAULT_SERVER_PORT;
        }

        final Server server = startServer();

        try {
            server.start();
            server.join();
        } finally {
            ServerState.kill();
            server.destroy();
        }

    }
}
