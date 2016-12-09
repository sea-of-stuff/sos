package uk.ac.standrews.cs.sos.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JettyApp {

    private static Server startServer(SOSLocalNode sos) throws Exception {

        assert sos != null;
        int port = sos.getHostAddress().getPort();

        RESTConfig restConfig = new RESTConfig();
        restConfig.setSOS(sos);

        ServletHolder servlet = new ServletHolder(new ServletContainer(restConfig));
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        return server;
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
