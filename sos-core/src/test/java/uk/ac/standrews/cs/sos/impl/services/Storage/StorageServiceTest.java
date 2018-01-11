package uk.ac.standrews.cs.sos.impl.services.Storage;

import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.services.StorageService;

import java.io.File;
import java.lang.reflect.Method;

import static uk.ac.standrews.cs.sos.constants.Paths.TEST_CONFIGURATIONS_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageServiceTest extends SetUpTest {

    protected StorageService storageService;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        storageService = localSOSNode.getStorageService();
    }

    @Override
    protected void createConfiguration() throws ConfigurationException {
        File file = new File(TEST_CONFIGURATIONS_PATH + "config_storage.json");

        settings = new SettingsConfiguration(file).getSettingsObj();
    }

}
