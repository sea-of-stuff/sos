package uk.ac.standrews.cs.sos.impl.context.defaults;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.examples.BinaryReplicationContext;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BinaryReplicationContextTest {

    @Test
    public void basicContextConstructor() throws NodesCollectionException {
        JsonNode emptyNode = JSONHelper.JsonObjMapper().createObjectNode();
        Context test = new BinaryReplicationContext(emptyNode, null,  "test", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(test.predicate());
    }
}
