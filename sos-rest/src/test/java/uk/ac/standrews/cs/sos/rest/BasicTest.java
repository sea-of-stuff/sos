package uk.ac.standrews.cs.sos.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;

import javax.ws.rs.core.Application;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicTest extends JerseyTestNg.ContainerPerMethodTest {

    @Override
    protected Application configure() {
        try {
            SeaConfiguration.setRootName("test-rest"); // FIXME - never called because it was set already somewhere else (perf-tests!)
            ServerState.startSOS();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResourceConfig()
                .packages("uk.ac.standrews.cs.sos.rest");
    }
}
