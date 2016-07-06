package uk.ac.standrews.cs.sos.rest;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HelloWorldTest extends BasicTest {

    @Test
    public void testHelloWorld() {
        final String hello = target("hello").request().get(String.class);
        assertEquals("Hello World!", hello);
    }

}