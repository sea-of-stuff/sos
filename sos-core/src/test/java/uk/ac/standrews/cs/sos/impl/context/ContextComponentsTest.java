package uk.ac.standrews.cs.sos.impl.context;

import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextComponentsTest {

    @Test
    public void dummy() throws IOException {
        String output = ContextComponents.ConstructClass("bla bla bla");
        System.out.println(output);
    }

}