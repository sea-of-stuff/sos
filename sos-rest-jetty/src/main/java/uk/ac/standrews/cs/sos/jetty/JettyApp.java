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

    public static Server startServer(String configFilePath) throws Exception {
        final ResourceConfig rc = new RESTConfig().build();

        ServerState.init(configFilePath);
        baseUri = uriBuilder.port(serverPort).build();
        return JettyHttpContainerFactory.createServer(baseUri, rc);
    }

    /**
     * Start a SOS instance and expose it via a Jetty Server.
     *
     * The following parameters are allowed:
     * - port (default port is 9998)
     * - TODO: configuration file
     * Example:
     *
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String configFilePath = "config.properties";
        if (args.length == 1) {
            configFilePath = args[0];
        }

        final Server server = startServer(configFilePath);

        try {
            server.start();
            server.join();
        } finally {
            ServerState.kill();
            server.destroy();
        }

    }
}
