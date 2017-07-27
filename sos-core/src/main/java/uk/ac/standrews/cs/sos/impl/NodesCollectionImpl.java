package uk.ac.standrews.cs.sos.impl;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesCollectionImpl implements NodesCollection {

    private TYPE type;
    private Set<IGUID> nodesRefs;

    public NodesCollectionImpl(TYPE type) throws NodesCollectionException {

        if (type.equals(TYPE.SPECIFIED)) throw new NodesCollectionException("Cannot use this constructor for Nodes Collection of type SPECIFIED");

        this.type = type;
    }

    public NodesCollectionImpl(TYPE type, Set<IGUID> nodesRefs) {
        this.type = type;
        this.nodesRefs = nodesRefs;
    }

    @Override
    public Set<IGUID> nodesRefs() {
        return nodesRefs;
    }

    @Override
    public TYPE type() {
        return type;
    }
}
