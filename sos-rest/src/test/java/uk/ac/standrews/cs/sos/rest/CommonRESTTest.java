package uk.ac.standrews.cs.sos.rest;

import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.ServerState;

import javax.ws.rs.core.Application;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonRESTTest extends JerseyTestNg.ContainerPerMethodTest  {

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();

        ServerState.init();
    }

    @AfterMethod
    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        ServerState.kill();
    }

    @Override
    protected Application configure() {
        return new RESTConfig().build();
    }

}
