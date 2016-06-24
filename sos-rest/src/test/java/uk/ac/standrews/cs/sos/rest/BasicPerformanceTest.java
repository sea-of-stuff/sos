package uk.ac.standrews.cs.sos.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import uk.ac.standrews.cs.sos.ServerState;

import javax.ws.rs.core.Application;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicPerformanceTest extends JerseyTestNg.ContainerPerClassTest {

    @Override
    protected Application configure() {
        ServerState.startSOS();
        return new ResourceConfig()
                .packages("uk.ac.standrews.cs.sos.rest");
    }
}
