package uk.ac.standrews.cs.sos.impl.context.defaults;

import org.junit.Test;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.examples.BinaryReplicationContext;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BinaryReplicationContextTest {

    @Test
    public void basicContextConstructor() throws NodesCollectionException {
        Context test = new BinaryReplicationContext(null, "test", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(test.predicate());
    }
}
