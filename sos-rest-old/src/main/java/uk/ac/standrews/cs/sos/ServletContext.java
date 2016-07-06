package uk.ac.standrews.cs.sos;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServletContext implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("INIT CONTEXT");

        System.out.println("Starting SOS");
        ServerState.startSOS();
        System.out.println("SOS started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
