package uk.ac.standrews.cs.sos.model.context;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextImplTest extends SetUpTest {

    @Test
    public void guidIsNotNull() {
        uk.ac.standrews.cs.sos.interfaces.model.Context context = new ContextImpl(localSOSNode.getAgent(), "test", null);

        assertNotNull(context.guid());
    }

    @Test
    public void nameIsNotNull() {
        uk.ac.standrews.cs.sos.interfaces.model.Context context = new ContextImpl(localSOSNode.getAgent(), "test", null);

        assertNotNull(context.getName());
    }

    @Test
    public void getName() {
        uk.ac.standrews.cs.sos.interfaces.model.Context context = new ContextImpl(localSOSNode.getAgent(), "test", null);

        assertEquals("test", context.getName());
    }

}