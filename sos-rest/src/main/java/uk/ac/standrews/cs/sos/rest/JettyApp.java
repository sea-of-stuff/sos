package uk.ac.standrews.cs.sos.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Logger;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JettyApp {

    private static UriBuilder uriBuilder = UriBuilder.fromUri("http://0.0.0.0/");

    private static Server startServer(SOSLocalNode sos, int port) throws Exception {

        final ResourceConfig rc = new RESTConfig().setSOS(sos);
        URI baseUri = uriBuilder.port(port).build();

        return JettyHttpContainerFactory.createServer(baseUri, rc);
    }

    public static void RUN(SOSLocalNode sos) throws Exception  {
        assert sos != null;

        int port = sos.getHostAddress().getPort();
        System.out.println("SOS REST will run at localhost:" + port);
        System.out.println("REST API documentation available at localhost:" + port +
                "/swagger.json OR localhost:" + port + "/swagger.yaml");

        //org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
        Server server = startServer(sos, port);

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
