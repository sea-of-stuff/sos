package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.protocol.Task;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CheckReplica extends Task {

    private int numberOfReplicas;
    private NodesCollection codomain;
    private IGUID guid;

    public CheckReplica(NodesCollection codomain, IGUID guid) {
        this.codomain = codomain;
        this.guid = guid;

        numberOfReplicas = 0;
    }

    @Override
    public void performAction() {

    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    // TODO - CHECK THE NUMBER OF REPLICAS for given data, manifest, metadata, etc
}
