package uk.ac.standrews.cs.sos.impl;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.IKey;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

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

        // TODO - this must be used for type specified only

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

    @Override
    public String toUniqueString() {

        String retval = type().toString();

        if (nodesRefs != null && !nodesRefs.isEmpty()) {

            retval += "Refs" + nodesRefs.stream()
                    .sorted(Comparator.comparing(IGUID::toMultiHash))
                    .map(IKey::toMultiHash)
                    .collect(Collectors.joining("."));
        }

        return retval;
    }
}
