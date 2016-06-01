package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest {

    protected SeaConfiguration configuration;

    @BeforeSuite
    public void suiteSetup() throws IOException {
        String HOME = System.getProperty("user.home");

        try (PrintWriter writer = new PrintWriter(HOME + "/config.txt")) {
            writer.println("abcdefg12345");
            writer.println("/sos/test/");
            writer.println("data/");
            writer.println("cached_data/");
            writer.println("index/");
            writer.println("manifests/");
            writer.println("keys/private.der");
            writer.println("keys/public.der");
        }
    }

    @AfterSuite
    public void suiteTearDown() {
        String HOME = System.getProperty("user.home");

        File file = new File(HOME + "/config.txt");
        file.delete();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        SeaConfiguration.setRootName("test");
        configuration = SeaConfiguration.getInstance();

        Helper.cleanDirectory(configuration.getDBDirectory());

        Node node = new SOSNode(GUIDFactory.recreateGUID("12345678"));
        configuration.setNode(node);
    }
}
