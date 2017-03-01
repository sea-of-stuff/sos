package uk.ac.standrews.cs.sos.model.context;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.context.Context;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextImplTest {

    @Test
    public void guidIsNotNull() {
        uk.ac.standrews.cs.sos.interfaces.model.Context context = new ContextImpl("test", null);

        assertNotNull(context.getGUID());
    }

    @Test
    public void nameIsNotNull() {
        uk.ac.standrews.cs.sos.interfaces.model.Context context = new ContextImpl("test", null);

        assertNotNull(context.getName());
    }

    @Test
    public void getName() {
        uk.ac.standrews.cs.sos.interfaces.model.Context context = new ContextImpl("test", null);

        assertEquals("test", context.getName());
    }

    @Test
    public void closureIsNotNull() {
        Context closure = mock(Context.class);
        uk.ac.standrews.cs.sos.interfaces.model.Context context = new ContextImpl("test", closure);

        assertNotNull(context.getClosure());
    }
}