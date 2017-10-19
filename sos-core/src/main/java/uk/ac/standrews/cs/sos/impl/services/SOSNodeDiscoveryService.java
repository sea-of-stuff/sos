package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.impl.node.*;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.GetNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.InfoNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.PingNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.RegisterNode;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
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
        SOS_LOG.log(LEVEL.INFO, "DDS - Registering node with GUID: " + nodeToRegister.guid().toMultiHash());

        try {
            manifestsDataService.addManifest(node);

            localNodesDirectory.addNode(nodeToRegister);
            localNodesDirectory.persistNodesTable();
        } catch (ManifestPersistException | NodesDirectoryException e) {
            throw new NodeRegistrationException("Unable to register node", e);
        }

        // Register the node to other NDS nodes
        if (!localOnly) {
            Set<Node> ndsNodes = getNodes(NodeType.NDS);
            ndsNodes.forEach(n -> {
                RegisterNode registerNode = new RegisterNode(node, n);
                TasksQueue.instance().performAsyncTask(registerNode);
            });
        }

        return nodeToRegister;
    }

    @Override
    public Node getNode(IGUID guid) throws NodeNotFoundException {

        // manifestsDataService.getManifest(guid);


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
    public Set<Node> getNodes(NodeType type) {

        switch(type) {
            case STORAGE:
                return localNodesDirectory.getNodes(Node::isStorage, NO_LIMIT);
            case NDS:
                return localNodesDirectory.getNodes(Node::isNDS, NO_LIMIT);
            case CMS:
                return localNodesDirectory.getNodes(Node::isCMS, NO_LIMIT);
            case DDS:
                return localNodesDirectory.getNodes(Node::isDDS, NO_LIMIT);
            case RMS:
                return localNodesDirectory.getNodes(Node::isRMS, NO_LIMIT);
            case MMS:
                return localNodesDirectory.getNodes(Node::isMMS, NO_LIMIT);
        }

        return Collections.emptySet();
    }

    @Override
    public Set<Node> getNodes(NodesCollection nodesCollection, int limit) {

        if (nodesCollection.type() == NodesCollectionType.ANY) {
            return getNodes(limit);
        }

        if (nodesCollection.type() == NodesCollectionType.LOCAL) {
            Set<Node> retval = new LinkedHashSet<>();
            retval.add(localNodesDirectory.getLocalNode());
            return retval;
        }

        Set<Node> retval = new LinkedHashSet<>();
        for(IGUID guid : nodesCollection.nodesRefs()) {

            try {
                retval.add(getNode(guid));
            } catch (NodeNotFoundException e) {
                continue;
            }

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
                    case DDS:
                        if (node.isDDS()) filteredNodes.add(nodeRef); break;
                    case RMS:
                        if (node.isRMS()) filteredNodes.add(nodeRef); break;
                    case MMS:
                        if (node.isMMS()) filteredNodes.add(nodeRef); break;
                }
            } catch (NodeNotFoundException e) {
                SOS_LOG.log(LEVEL.WARN, "Unable to get node with ref: " + nodeRef);
            }

            if (filteredNodes.size() == limit && limit != NO_LIMIT)
                break;
        }

        return new NodesCollectionImpl(filteredNodes);
    }

    @Override
    public Set<Node> getNodes() {
        return getNodes(NO_LIMIT);
    }

    @Override
    public Set<Node> getNodes(int limit) {
        return localNodesDirectory.getNodes(p -> true, limit);
    }

    @Override
    public String infoNode(IGUID guid) throws NodeNotFoundException {
        Node node = getNode(guid);

        InfoNode infoNode = new InfoNode(node);
        TasksQueue.instance().performSyncTask(infoNode);

        String retval = infoNode.getInfo();
        if (retval == null) {
            throw new NodeNotFoundException("Unable to find info about node");
        } else {
            return retval;
        }
    }

    @Override
    public String infoNode(Node node) throws NodeNotFoundException {

        InfoNode infoNode = new InfoNode(node);
        TasksQueue.instance().performSyncTask(infoNode);

        String retval = infoNode.getInfo();
        if (retval == null) {
            throw new NodeNotFoundException("Unable to find info about node");
        } else {
            return retval;
        }
    }

    @Override
    public NodeStats getNodeStats(IGUID guid) {
        if (!nodesStats.containsKey(guid)) {
            nodesStats.put(guid, new NodeStats(guid));
        }

        return nodesStats.get(guid);
    }

    /**
     * TODO - use getManifest task via MDS
     *
     * Find a matching node for the given GUID through other known NDS nodes
     */
    private Node findNodeViaNDS(IGUID nodeGUID) throws NodeNotFoundException {

        Set<Node> ndsNodes = getNodes(NodeType.NDS);
        GetNode getNode = new GetNode(nodeGUID, ndsNodes.iterator());
        TasksQueue.instance().performSyncTask(getNode);

        Node retval = getNode.getFoundNode();
        if (retval == null) {
            throw new NodeNotFoundException("Unable to find node with GUID " + nodeGUID);
        }

        try {
            registerNode(retval, true);
        } catch (NodeRegistrationException ignored) { }

        return retval;
    }

    private void runCheckNodesPeriodic() {

        service.scheduleWithFixedDelay(() -> {

            for(Node node:getNodes()) {

                if (!nodesStats.containsKey(node.guid())) {
                    nodesStats.put(node.guid(), new NodeStats(node.guid()));
                }

                PingNode pingNode = new PingNode(node, UUID.randomUUID().toString());
                TasksQueue.instance().performSyncTask(pingNode);
                nodesStats.get(node.guid()).addMeasure(pingNode.getTimestamp(), pingNode.valid(), pingNode.getLatency());
            }

        }, 10, 10, TimeUnit.SECONDS);
        // }, predicateThreadSettings.getInitialDelay(), predicateThreadSettings.getPeriod(), TimeUnit.SECONDS);
    }
}
