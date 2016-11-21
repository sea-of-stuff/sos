package uk.ac.standrews.cs.sos.model.context;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.context.Closure;
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
        Context context = new ContextImpl("test", null);

        assertNotNull(context.getGUID());
    }

    @Test
    public void nameIsNotNull() {
        Context context = new ContextImpl("test", null);

        assertNotNull(context.getName());
    }

    @Test
    public void getName() {
        Context context = new ContextImpl("test", null);

        assertEquals("test", context.getName());
    }

    @Test
    public void closureIsNotNull() {
        Closure closure = mock(Closure.class);
        Context context = new ContextImpl("test", closure);

        assertNotNull(context.getClosure());
    }
}