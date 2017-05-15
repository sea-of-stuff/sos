package uk.ac.standrews.cs.sos.impl.context.defaults;

import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.impl.context.BaseContext;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TestContext extends BaseContext {

    public TestContext(SOSAgent agent, Predicate<Version> predicate) {
        super(agent, predicate);
    }

    public String toString() {
        return "test worked";
    }
}
