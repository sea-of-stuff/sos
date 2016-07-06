package uk.ac.standrews.cs.sos;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServletContext implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
       ServerState.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
