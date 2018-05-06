/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module rest-jetty.
 *
 * rest-jetty is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * rest-jetty is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with rest-jetty. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Logger;
import org.glassfish.jersey.servlet.ServletContainer;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.rest.RESTConfig;

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
            System.out.println("NODE GUID: " + sos.guid().toMultiHash());
            System.out.println("Starting REST server on port: " + sos.getHostAddress().getPort());
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
