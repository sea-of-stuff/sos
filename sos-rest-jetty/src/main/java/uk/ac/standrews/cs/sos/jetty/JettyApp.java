package uk.ac.standrews.cs.sos.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Logger;
import org.glassfish.jersey.servlet.ServletContainer;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

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
        org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
        Server server = startServer(sos);

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

    /**
     * Enable logging by overriding the functions below and setting the LOG level in
     * /resources/jetty-logging.properties
     */
    public static class NoLogging implements Logger {
        @Override public String getName() { return "no"; }
        @Override public void warn(String msg, Object... args) { }
        @Override public void warn(Throwable thrown) { }
        @Override public void warn(String msg, Throwable thrown) { }
        @Override public void info(String msg, Object... args) { }
        @Override public void info(Throwable thrown) { }
        @Override public void info(String msg, Throwable thrown) { }
        @Override public boolean isDebugEnabled() { return false; }
        @Override public void setDebugEnabled(boolean enabled) { }
        @Override public void debug(String msg, Object... args) { }
        @Override public void debug(String msg, long value) {}
        @Override public void debug(Throwable thrown) { }
        @Override public void debug(String msg, Throwable thrown) { }
        @Override public Logger getLogger(String name) { return this; }
        @Override public void ignore(Throwable ignored) { }
    }
}
