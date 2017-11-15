package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.IKey;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.NodesCollectionType;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesCollectionImpl implements NodesCollection {

    private NodesCollectionType type;
    private Set<IGUID> nodesRefs;

    public NodesCollectionImpl(NodesCollectionType type) throws NodesCollectionException {

        if (type.equals(NodesCollectionType.SPECIFIED)) throw new NodesCollectionException("Cannot use this constructor for Nodes Collection of type SPECIFIED");

        this.type = type;
        this.nodesRefs = new LinkedHashSet<>();
    }

    public NodesCollectionImpl(IGUID local) {

        this.type = NodesCollectionType.LOCAL;
        this.nodesRefs = new LinkedHashSet<>();
        this.nodesRefs.add(local);
    }

    public NodesCollectionImpl(Set<IGUID> nodesRefs) {

        this.type = NodesCollectionType.SPECIFIED;
        this.nodesRefs = nodesRefs;
    }

    @Override
    public Set<IGUID> nodesRefs() {
        return nodesRefs;
    }

    @Override
    public NodesCollectionType type() {
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
