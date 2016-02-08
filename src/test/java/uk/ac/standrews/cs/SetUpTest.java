package uk.ac.standrews.cs;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest {

    @BeforeSuite
    public void suiteSetup() throws IOException {
        String HOME = System.getProperty("user.home");

        PrintWriter writer = new PrintWriter(HOME + "/config.txt");
        writer.println("abcdefg12345");
        writer.println("/sos/test/");
        writer.println("data/");
        writer.println("cached_data/");
        writer.println("index/");
        writer.println("manifests/");
        writer.println("keys/private.der");
        writer.println("keys/public.der");
        writer.close();
    }

    @AfterSuite
    public void suiteTearDown() {
        String HOME = System.getProperty("user.home");

        File file = new File(HOME + "/config.txt");
        file.delete();
    }
}
