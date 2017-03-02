package uk.ac.standrews.cs.sos.model.context.defaults;

import uk.ac.standrews.cs.sos.actors.SOSAgent;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.SOSMetadata;
import uk.ac.standrews.cs.sos.model.context.ContextImpl;

import java.util.function.Predicate;


import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.context.Rule;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Context;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TestContext extends ContextImpl {

    public TestContext(SOSAgent agent, Predicate<Asset> predicate) {
        super(agent, predicate);
    }

    public String toString() {
        return "test worked";
    }
}
