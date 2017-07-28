package uk.ac.standrews.cs.sos.impl.services.Storage;

import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.services.Storage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static uk.ac.standrews.cs.sos.constants.Paths.TEST_CONFIGURATIONS_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageTest extends SetUpTest {

    protected Storage storage;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        storage = localSOSNode.getStorage();
    }

    @Override
    protected void createConfiguration() throws ConfigurationException, IOException {
        File file = new File(TEST_CONFIGURATIONS_PATH + "config_storage.json");

        settings = new SettingsConfiguration(file).getSettingsObj();
    }

}
