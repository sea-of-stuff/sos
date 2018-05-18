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
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static uk.ac.standrews.cs.sos.impl.services.SOSNodeDiscoveryService.NO_LIMIT;

/**
 * The nodes directory which keeps track of the known nodes at this given node
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalNodesDirectory {

    private Node localNode;
    private NodesDatabase nodesDatabase;

    private final Set<Node> knownNodes;

    public LocalNodesDirectory(Node localNode, NodesDatabase nodesDatabase) throws NodesDirectoryException {
        this.localNode = localNode;
        this.nodesDatabase = nodesDatabase;

        this.knownNodes = new HashSet<>(); // Order not preserved
        loadNodesFromDB();
    }

    /**
     * Add/update an arbitrary node to the directory.
     * This will be used to discovery nodes/data in the LocalSOSNode.
     *
     * @param node to be added
     */
    public void addNode(Node node) {

        synchronized (knownNodes) {
            SOSNode clone = new SOSNode(node);
            knownNodes.remove(node);
            knownNodes.add(clone);
        }

    }

    /**
     * Get a Node node given its guid identifier.
     *
     * @param guid for the node
     * @return matching node
     */
    public Node getNode(IGUID guid) {
        Optional<Node> node = knownNodes.stream()
                .filter(n -> n.guid().equals(guid))
                .findFirst();

        return node.orElse(null);
    }

    /**
     * Get the nodes matching the predicate and within the given limit
     *
     * @param predicate (e.g. Node::isAgent, Node::isStorage)
     * @param limit max number of nodes to return, ignore if limit <= 0
     * @return set of nodes
     */
    public Set<IGUID> getNodes(Predicate<Node> predicate, int limit) {

        Stream<IGUID> nodesStream = knownNodes.stream()
                .filter(predicate)
                .map(Manifest::guid);

        if (getLocalNode() != null) {
            nodesStream = nodesStream.filter(n -> !n.equals(getLocalNode().guid()));
        }

        nodesStream = nodesStream.distinct();

        if (limit > NO_LIMIT) {
            nodesStream = nodesStream.limit(limit);
        }

        List<IGUID> nodes = nodesStream.collect(toList());
        Collections.shuffle(nodes); // Naive load balancing

        return new HashSet<>(nodes);
    }

    /**
     * Get the local node running
     *
     * @return local node
     */
    public Node getLocalNode() {
        return this.localNode;
    }

    /**
     * Persist the collection of known nodes.
     *
     * @throws NodesDirectoryException if nodes table cannot be persisted
     */
    public void persistNodesTable() throws NodesDirectoryException {
        try {
            for (Node knownNode : knownNodes) {
                nodesDatabase.addNode(knownNode);
            }
        } catch (DatabaseConnectionException e) {
            throw new NodesDirectoryException(e);
        }
    }

    public void clear() {

        knownNodes.clear();
    }

    /**
     * Load nodes from the DB to the node directory (in memory)
     *
     * @throws NodesDirectoryException if nodes cannot be loaded
     */
    private void loadNodesFromDB() throws NodesDirectoryException {
        try {
            Set<SOSNode> nodes = nodesDatabase.getNodes();
            knownNodes.addAll(nodes);
        } catch (DatabaseConnectionException e) {
            throw new NodesDirectoryException(e);
        }
    }
}
