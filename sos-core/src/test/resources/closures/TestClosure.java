package uk.ac.standrews.cs.sos.model.context.closures;

import uk.ac.standrews.cs.sos.actors.SOSAgent;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TestClosure extends BaseClosure {

    public TestClosure() {
        super(null, null);
    }

    public String toString() {
        return "test worked";
    }

    protected TestClosure(SOSAgent agent, Predicate<Asset> predicate) {
        super(agent, predicate);
    }
}
