package uk.ac.standrews.cs.sos.impl.context.defaults;

import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.impl.context.ContextImpl;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TestContext extends ContextImpl {

    public TestContext(SOSAgent agent, Predicate<Version> predicate) {
        super(agent, predicate);
    }

    public String toString() {
        return "test worked";
    }
}
