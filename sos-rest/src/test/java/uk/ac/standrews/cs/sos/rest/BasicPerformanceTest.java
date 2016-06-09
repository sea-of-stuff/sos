package uk.ac.standrews.cs.sos.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.model.Configuration;

import javax.ws.rs.core.Application;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicPerformanceTest extends JerseyTestNg.ContainerPerClassTest {

    @Override
    protected Application configure() {
        Configuration.setRootName("test-rest-perf");
        ServerState.startSOS();
        return new ResourceConfig()
                .packages("uk.ac.standrews.cs.sos.rest");
    }
}
