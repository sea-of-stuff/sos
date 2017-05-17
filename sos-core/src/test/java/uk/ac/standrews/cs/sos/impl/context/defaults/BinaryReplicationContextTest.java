package uk.ac.standrews.cs.sos.impl.context.defaults;

import org.junit.Test;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.examples.BinaryReplicationContext;
import uk.ac.standrews.cs.sos.model.Context;

import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BinaryReplicationContextTest {

    @Test
    public void basicContextConstructor() {
        Context test = new BinaryReplicationContext("test", new NodesCollectionImpl(), new NodesCollectionImpl());

        assertNotNull(test.predicate());
    }
}