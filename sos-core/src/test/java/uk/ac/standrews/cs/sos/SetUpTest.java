package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.sos.url.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.network.Node;
import uk.ac.standrews.cs.sos.network.NodeManager;
import uk.ac.standrews.cs.sos.network.SOSNode;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

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

        Node node = new SOSNode(GUIDFactory.recreateGUID("12345678"));
        configuration.setNode(node);

        NodeManager nodeManager = new NodeManager();
        try {
            URL.setURLStreamHandlerFactory(new SOSURLStreamHandlerFactory(nodeManager));
        } catch (Error e) {
            // Error is thrown if factory was already setup in previous tests
        }
    }
}
