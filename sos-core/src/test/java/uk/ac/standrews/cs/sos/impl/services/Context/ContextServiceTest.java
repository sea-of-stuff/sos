package uk.ac.standrews.cs.sos.impl.services.Context;

import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.services.Agent;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static uk.ac.standrews.cs.sos.constants.Paths.TEST_CONFIGURATIONS_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextServiceTest extends SetUpTest {

    protected ContextService contextService;
    protected Agent agent;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        contextService = localSOSNode.getCMS();
        agent = localSOSNode.getAgent();
    }

    @Override
    protected void createConfiguration() throws ConfigurationException, IOException {
        File file = new File(TEST_CONFIGURATIONS_PATH + "config_context.json");

        settings = new SettingsConfiguration(file).getSettingsObj();
    }
}
