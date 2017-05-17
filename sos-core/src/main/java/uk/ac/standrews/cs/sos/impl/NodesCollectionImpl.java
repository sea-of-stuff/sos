package uk.ac.standrews.cs.sos.impl;

import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesCollectionImpl implements NodesCollection {

    private TYPE type;
    private Set<Node> nodes;

    public NodesCollectionImpl(TYPE type) {
        this.type = type;

        // TODO - make sure that type is not specified
    }

    public NodesCollectionImpl(TYPE type, Set<Node> nodes) {
        this.type = type;
        this.nodes = nodes;
    }


    @Override
    public Set<Node> nodes() {
        return nodes;
    }

    @Override
    public TYPE type() {
        return type;
    }
}
