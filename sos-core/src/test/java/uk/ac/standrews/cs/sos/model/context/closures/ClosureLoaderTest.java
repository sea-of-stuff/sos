package uk.ac.standrews.cs.sos.model.context.closures;

import org.testng.annotations.Test;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClosureLoaderTest {

    @Test
    public void dummy() {
        ClosureLoader loader = new ClosureLoader();
        loader.load("/Users/sic2/", "TestClosure");
    }
}