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
package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.impl.node.*;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.InfoNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.PingNode;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.NodesCollectionType;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The SOSNDS represents a basic NDS implementation.
 * It provides naive methods to register new nodes in the sos and get known nodes.
 *
 * The NDS uses a combination of the ManifestsDataService and its own DB to manage nodes as first class entities, while
 * providing advanced nodes management via the DB.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNodeDiscoveryService implements NodeDiscoveryService {

    private ManifestsDataService manifestsDataService;

    public static final int NO_LIMIT = 0;
    private LocalNodesDirectory localNodesDirectory;

    private static final int NDS_SCHEDULER_PS = 1;
    private ScheduledExecutorService service;
    private HashMap<IGUID, NodeStats> nodesStats;

    public SOSNodeDiscoveryService(Node localNode, NodesDatabase nodesDatabase) throws NodesDirectoryException {
        localNodesDirectory = new LocalNodesDirectory(localNode, nodesDatabase);

        nodesStats = new LinkedHashMap<>();

        boolean ping = SOSLocalNode.settings.getServices().getNds().isPing();
        if (ping) {
            service = new ScheduledThreadPoolExecutor(NDS_SCHEDULER_PS);
            runCheckNodesPeriodic();
        }
    }

    @Override
    public void setMDS(ManifestsDataService manifestsDataService) {
        this.manifestsDataService = manifestsDataService;
    }

    @Override
    public Node getThisNode() {
        return localNodesDirectory.getLocalNode();
    }

    @Override
    public Node registerNode(Node node, boolean localOnly) throws NodeRegistrationException {
        if (node == null) {
            throw new NodeRegistrationException("Invalid node");
        }

        Node nodeToRegister = new SOSNode(node);
        SOS_LOG.log(LEVEL.INFO, "NDS - Registering node with GUID: " + nodeToRegister.guid().toMultiHash());

        try {
            localNodesDirectory.addNode(nodeToRegister);
            localNodesDirectory.persistNodesTable();

            if (localOnly) {
                manifestsDataService.addManifest(node);
            } else {
                manifestsDataService.addManifest(node, true, new NodesCollectionImpl(NodesCollectionType.ANY), 1, true);
            }

        } catch (ManifestPersistException | NodesDirectoryException | NodesCollectionException e) {
            throw new NodeRegistrationException("Unable to register node", e);
        }

        return nodeToRegister;
    }

    @Override
    public Node getNode(IGUID guid) throws NodeNotFoundException {

        if (guid == null || guid.isInvalid()) {
            throw new NodeNotFoundException("Cannot find node for invalid GUID");
        }

        Node localNode = localNodesDirectory.getLocalNode();
        if (localNode.guid().equals(guid)) {
            return localNode;
        }

        Node nodeToContact = localNodesDirectory.getNode(guid);

        if (nodeToContact == null) {
            nodeToContact = findNodeViaNDS(guid);
        }

        if (nodeToContact == null) {
            throw new NodeNotFoundException("Unable to find node for GUID: " + guid.toString());
        } else {
            SOS_LOG.log(LEVEL.INFO, "Node with GUID " + guid + " was found");
        }

        return nodeToContact;
    }

    @Override
    public Set<IGUID> getNodes(NodeType type) {

        switch(type) {
            case STORAGE:
                return localNodesDirectory.getNodes(Node::isStorage, NO_LIMIT);
            case NDS:
                return localNodesDirectory.getNodes(Node::isNDS, NO_LIMIT);
            case CMS:
                return localNodesDirectory.getNodes(Node::isCMS, NO_LIMIT);
            case MDS:
                return localNodesDirectory.getNodes(Node::isMDS, NO_LIMIT);
            case RMS:
                return localNodesDirectory.getNodes(Node::isRMS, NO_LIMIT);
            case MMS:
                return localNodesDirectory.getNodes(Node::isMMS, NO_LIMIT);
        }

        return Collections.emptySet();
    }

    @Override
    public Set<IGUID> getNodes(NodesCollection nodesCollection, int limit) {

        if (nodesCollection.type() == NodesCollectionType.ANY) {
            return getNodes(limit);
        }

        if (nodesCollection.type() == NodesCollectionType.LOCAL) {
            Set<IGUID> retval = new LinkedHashSet<>();
            retval.add(localNodesDirectory.getLocalNode().guid());
            return retval;
        }

        Set<IGUID> retval = new LinkedHashSet<>();
        for(IGUID guid : nodesCollection.nodesRefs()) {
            retval.add(guid);

            if (retval.size() > limit) break;
        }


        return retval;
    }

    @Override
    public NodesCollection filterNodesCollection(NodesCollection nodesCollection, NodeType type, int limit) {

        if (nodesCollection.type() == NodesCollectionType.ANY && limit == NO_LIMIT) {
            return nodesCollection;
        }

        if (nodesCollection.type() == NodesCollectionType.LOCAL) {
            return new NodesCollectionImpl(localNodesDirectory.getLocalNode().guid());
        }

        Set<IGUID> nodesRefs = nodesCollection.nodesRefs();
        if (nodesRefs == null || nodesRefs.isEmpty()) {
            return new NodesCollectionImpl(new LinkedHashSet<>());
        }

        Set<IGUID> filteredNodes = new LinkedHashSet<>();
        for(IGUID nodeRef : nodesRefs) {

            // COMPARE node's type with type passed in this method
            // and
            // add node to filteredNodes
            try {
                Node node = getNode(nodeRef);
                switch(type) {
                    case STORAGE:
                        if (node.isStorage()) filteredNodes.add(nodeRef); break;
                    case NDS:
                        if (node.isNDS()) filteredNodes.add(nodeRef); break;
                    case CMS:
                        if (node.isCMS()) filteredNodes.add(nodeRef); break;
                    case MDS:
                        if (node.isMDS()) filteredNodes.add(nodeRef); break;
                    case RMS:
                        if (node.isRMS()) filteredNodes.add(nodeRef); break;
                    case MMS:
                        if (node.isMMS()) filteredNodes.add(nodeRef); break;
                    default:
                        throw new NodeNotFoundException("Node type is unknown");
                }
            } catch (NodeNotFoundException e) {
                SOS_LOG.log(LEVEL.WARN, "SOSNodeDiscovery - Unable to get node with ref: " + nodeRef);
            }

            if (filteredNodes.size() == limit && limit != NO_LIMIT)
                break;
        }

        return new NodesCollectionImpl(filteredNodes);
    }

    @Override
    public NodesCollection filterNodesCollection(NodesCollection nodesCollection, int limit) {

        Set<IGUID> filteredNodes = getNodes(nodesCollection, limit);
        return new NodesCollectionImpl(filteredNodes);
    }

    @Override
    public Set<IGUID> getNodes() {
        return getNodes(NO_LIMIT);
    }

    @Override
    public Set<IGUID> getNodes(int limit) {
        return localNodesDirectory.getNodes(p -> true, limit);
    }

    @Override
    public String infoNode(IGUID guid) throws NodeNotFoundException {
        Node node = getNode(guid);

        InfoNode infoNode = new InfoNode(node);
        TasksQueue.instance().performSyncTask(infoNode);
        if (infoNode.getState() == TaskState.SUCCESSFUL) {

            String retval = infoNode.getInfo();
            // TODO - are info about node updated??
            if (retval != null) {
                return retval;
            }

        }

        throw new NodeNotFoundException("Unable to find info about node");
    }

    @Override
    public String infoNode(Node node) throws NodeNotFoundException {

        InfoNode infoNode = new InfoNode(node);
        TasksQueue.instance().performSyncTask(infoNode);
        if (infoNode.getState() == TaskState.SUCCESSFUL) {

            String retval = infoNode.getInfo();
            if (retval != null) {
                return retval;
            }

        }

        throw new NodeNotFoundException("Unable to find info about node");
    }

    @Override
    public NodeStats getNodeStats(IGUID guid) {
        if (!nodesStats.containsKey(guid)) {
            nodesStats.put(guid, new NodeStats(guid));
        }

        return nodesStats.get(guid);
    }

    /**
     * Find a matching node for the given GUID through other known NDS nodes
     */
    private Node findNodeViaNDS(IGUID nodeGUID) throws NodeNotFoundException {

        try {
            Manifest manifest = manifestsDataService.getManifest(nodeGUID, NodeType.NDS);
            Node node = (Node) manifest;
            registerNode(node, true);

            return node;

        } catch (ManifestNotFoundException e) {
            throw new NodeNotFoundException("Unable to find node with GUID " + nodeGUID);
        } catch (NodeRegistrationException e) {
            throw new NodeNotFoundException("Unable to register node with GUID (but it was found)" + nodeGUID);
        }

    }

    private void runCheckNodesPeriodic() {

        service.scheduleWithFixedDelay(() -> {

            for(IGUID nodeRef:getNodes()) {

                try {
                    if (!nodesStats.containsKey(nodeRef)) {
                        nodesStats.put(nodeRef, new NodeStats(nodeRef));
                    }

                    Node node = getNode(nodeRef);
                    PingNode pingNode = new PingNode(node, UUID.randomUUID().toString(), true);
                    TasksQueue.instance().performSyncTask(pingNode);
                    nodesStats.get(nodeRef).addMeasure(pingNode.getTimestamp(), pingNode.valid(), pingNode.getLatency());

                } catch (NodeNotFoundException e) {
                    SOS_LOG.log(LEVEL.WARN, "Unable to ping node with GUID " + nodeRef.toShortString());
                    continue;
                }
            }

        }, 10, 10, TimeUnit.SECONDS); // TODO - use settings from node config
    }

    @Override
    public void flush() {

    }

    @Override
    public void shutdown() {

        if (service != null) {
            service.shutdown();
        }

        localNodesDirectory.clear();
        nodesStats = new LinkedHashMap<>();
    }
}
