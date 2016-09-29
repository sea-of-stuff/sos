package uk.ac.standrews.cs.sos.jetty;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JettyApp {

    private static UriBuilder uriBuilder = UriBuilder.fromUri("http://0.0.0.0/");
    private static URI baseUri;

    private static Server startServer(String configFilePath) throws Exception {
        final ResourceConfig rc = new RESTConfig().build();

        SOSLocalNode sos = ServerState.init(configFilePath);
        assert sos != null;
        int port = sos.getHostAddress().getPort();

        baseUri = uriBuilder.port(port).build();
        return JettyHttpContainerFactory.createServer(baseUri, rc);
    }

    /**
     * Start a SOS instance and expose it via a Jetty Server.
     *
     * The following parameters are allowed:
     * - configuration file path
     *
     * @param args configuration file path
     * @throws Exception thrown if the jetty server could not be started properly
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
