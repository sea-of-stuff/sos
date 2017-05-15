package uk.ac.standrews.cs.sos.impl.context.defaults;

import org.junit.Test;
import uk.ac.standrews.cs.sos.impl.context.examples.TextContext;
import uk.ac.standrews.cs.sos.model.Context;

import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TextContextTest {

    @Test
    public void dummy() {
        Context test = new TextContext()
                .setName("test")
                .setSources(null) // TODO - set to local node!
                .build();

        assertNotNull(test.predicate());
    }
}