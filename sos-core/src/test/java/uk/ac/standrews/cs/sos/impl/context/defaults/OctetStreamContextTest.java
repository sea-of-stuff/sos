package uk.ac.standrews.cs.sos.impl.context.defaults;

import org.junit.Test;
import uk.ac.standrews.cs.sos.impl.context.examples.OctetStreamContext;
import uk.ac.standrews.cs.sos.model.Context;

import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class OctetStreamContextTest {

    @Test
    public void basicContextConstructor() {
        Context test = new OctetStreamContext("test");

        assertNotNull(test.predicate());
    }
}