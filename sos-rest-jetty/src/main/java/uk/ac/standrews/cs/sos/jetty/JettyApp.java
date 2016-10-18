package uk.ac.standrews.cs.sos.jetty;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JettyApp {

    private static UriBuilder uriBuilder = UriBuilder.fromUri("http://0.0.0.0/");
    private static URI baseUri;

    private static Server startServer(SOSLocalNode sos) throws Exception {
        final ResourceConfig rc = new RESTConfig().build(sos);

        assert sos != null;
        int port = sos.getHostAddress().getPort();
        baseUri = uriBuilder.port(port).build();
        System.out.println("Starting REST Service on port: " + port);

        return JettyHttpContainerFactory.createServer(baseUri, rc);
    }

    public static void RUN(SOSLocalNode sos) throws Exception  {
        Server server = startServer(sos);

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
