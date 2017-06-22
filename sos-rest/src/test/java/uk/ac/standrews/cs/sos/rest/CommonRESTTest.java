package uk.ac.standrews.cs.sos.rest;

import org.glassfish.jersey.test.JerseyTestNg;
import org.glassfish.jersey.test.TestProperties;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.RESTConfig;

import javax.ws.rs.core.Application;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonRESTTest extends JerseyTestNg.ContainerPerMethodTest  {

    protected ServerState state;
    protected RESTConfig config;

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();

        ClassLoader classLoader = getClass().getClassLoader();
        String path = classLoader.getResource("config.conf").getFile();

        state = new ServerState();
        state.init(path);

        config.setSOS(state.sos);
    }

    @AfterMethod
    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        state.kill();
    }

    @Override
    protected Application configure() {

        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        config = new RESTConfig();
        return config;
    }

}
