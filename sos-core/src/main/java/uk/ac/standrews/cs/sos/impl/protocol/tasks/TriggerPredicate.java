package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TriggerPredicate extends Task {

    private Node node;
    private IGUID context;

    public TriggerPredicate(Node node, IGUID context) {
        this.node = node;
        this.context = context;
    }

    @Override
    protected void performAction() {

    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Task deserialize(String json) throws IOException {
        return null;
    }
}
