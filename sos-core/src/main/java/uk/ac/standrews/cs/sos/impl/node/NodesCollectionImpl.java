/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.IKey;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.NodesCollectionType;

import java.util.*;
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
    public void addNodeRef(IGUID nodeRef) {

        if (type == NodesCollectionType.SPECIFIED) {
            nodesRefs.add(nodeRef);
        }
    }

    @Override
    public int size() {
        return nodesRefs.size();
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

    @Override
    public synchronized void shuffle() {

        List<IGUID> nodes = new ArrayList<>(nodesRefs);
        Collections.shuffle(nodes);
        this.nodesRefs = new LinkedHashSet<>(nodes);
    }
}
