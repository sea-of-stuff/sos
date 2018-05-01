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
package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.IKey;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.datamodel.AtomManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.impl.protocol.json.DataPackage;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.services.StorageService;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * The AtomReplication task, as the name suggests, replicates data to other nodes.
 * The data can be replicated only to Storage nodes.
 * In doing the replication the caller MUST also specify a wished replication factor for the data.
 *
 *
 * Input for the AtomReplication task:
 * - data to be replicated
 * - list of nodes where the data CAN be replicated
 * - replication factor to satisfy
 *
 * Additionally, the task needs access to the following SOS local services:
 * - storage
 * - node discovery
 * - data discovery
 *
 *
 * When data is replicated successfully to a node
 * - the storage service is informed about the new location for the data
 *
 *
 * If the data is successfully replicated to a Storage node:
 * - TODO - write down how the internal data structures change
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomReplication extends Task {

    private IGUID guid;
    private Data data;
    private NodesCollection nodesCollection;
    private int replicationFactor;
    private boolean delegateReplication;
    private boolean dataIsAlreadyProtected;
    private boolean sequential;

    private StorageService storageService;
    private NodeDiscoveryService nodeDiscoveryService;

    /**
     * Replicates the data at least n times within the specified nodes collection.
     * If the delegateReplication parameter is true, then the data is sent to a storage node (within the nodes collection),
     * which will take care of replicating the data on behalf of this node. This storage node will replicate the data
     * to the nodes specified in the nodesCollection.
     *
     *
     * Construct the data replication task.
     * The data, nodes and replication factor paramters are needed to carry out the task
     * The index, nds and mds are needed to promptly update this node about the new replicated content.
     */
    public AtomReplication(IGUID guid, Data data, NodesCollection nodesCollection, int replicationFactor,
                           StorageService storageService, NodeDiscoveryService nodeDiscoveryService,
                           boolean delegateReplication, boolean dataIsAlreadyProtected, boolean sequential) throws SOSProtocolException {
        super();

        if (storageService == null || nodeDiscoveryService == null) {
            setState(TaskState.ERROR);
            throw new SOSProtocolException("At least one of the SOS services is null. Data replication process is aborted.");
        }

        this.storageService = storageService;
        this.nodeDiscoveryService = nodeDiscoveryService;

        this.guid = guid;
        this.data = data;
        this.nodesCollection = nodesCollection;
        this.replicationFactor = replicationFactor;
        this.delegateReplication = delegateReplication;
        this.dataIsAlreadyProtected = dataIsAlreadyProtected;
        this.sequential = sequential;
    }

    @Override
    public void performAction() {

        if (sequential) {
            sequentialDataReplication();
        } else {
            parallelDataReplication();
        }
    }

    private void sequentialDataReplication() {

        try (final InputStream inputStream = data.getInputStream();
             final ByteArrayOutputStream baos = IO.InputStreamToByteArrayOutputStream(inputStream)) {

            int successfulReplicas = 0;
            Iterator<IGUID> nodeRefs = nodesCollection.nodesRefs().iterator();
            while (nodeRefs.hasNext() && successfulReplicas < replicationFactor) {

                IGUID ref = nodeRefs.next();
                boolean successful = replicate(baos, ref);
                if (successful) {
                    successfulReplicas++;
                }
            }

            checkReplicaConditionAndSetTaskState(successfulReplicas);

        } catch (IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "An exception occurred while replicating data");
        }

    }

    private void parallelDataReplication() {

        try (final InputStream inputStream = data.getInputStream();
             final ByteArrayOutputStream baos = IO.InputStreamToByteArrayOutputStream(inputStream)) {

            int poolSize = SOSLocalNode.settings.getServices().getStorage().getReplicationThreads();
            Executor executor = Executors.newFixedThreadPool(poolSize);
            CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);

            for (IGUID guid : nodesCollection.nodesRefs()) {
                completionService.submit(() -> replicate(baos, guid));
            }

            int numberOfCalls = nodesCollection.size();
            int received = 0;
            boolean errors = false;
            int successfulReplicas = 0;
            while (received < numberOfCalls && !errors && successfulReplicas < replicationFactor) {

                Future<Boolean> resultFuture = completionService.take(); //blocks if none available
                try {
                    Boolean result = resultFuture.get();
                    received++;

                    if (result) successfulReplicas++;
                } catch (Exception e) {
                    errors = true;
                }
            }

            checkReplicaConditionAndSetTaskState(successfulReplicas);
            ((ExecutorService) executor).shutdown();

        } catch (IOException | InterruptedException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "An exception occurred while replicating data");
        }
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Task deserialize(String json) throws IOException {
        return null;
    }

    private boolean replicate(ByteArrayOutputStream baos, IGUID iguid) {

        try (InputStream dataClone = new ByteArrayInputStream(baos.toByteArray())) {

            Node node = nodeDiscoveryService.getNode(iguid);
            if (!node.isStorage()) return false;

            boolean transferWasSuccessful = transferDataAndUpdateNodeState(dataClone, node, storageService);

            if (transferWasSuccessful) return true;

        } catch (IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "IOException - Unable to perform replication at node with ref: " + iguid);
        } catch (NodeNotFoundException e) {
            SOS_LOG.log(LEVEL.ERROR, "NodeNotFoundException - Unable to perform replication at node with ref: " + iguid);
        }

        return false;
    }

    private void checkReplicaConditionAndSetTaskState(int successfulReplicas) {

        if (successfulReplicas >= replicationFactor) {
            setState(TaskState.SUCCESSFUL);
        } else {
            setState(TaskState.UNSUCCESSFUL);
        }
    }

    /**
     * Transfer a stream of data to a given node and update this node state
     *
     * @param data to be transferred
     * @param node of destination
     * @param storageService of this node that has to be updated with new location
     * @return true if the data was transferred successfully.
     */
    private boolean transferDataAndUpdateNodeState(InputStream data, Node node, StorageService storageService) {

        try {
            Atom atom = transferDataRequest(data, node);
            SOS_LOG.log(LEVEL.INFO, "Successful data replication to node " + node.guid().toMultiHash());

            for(LocationBundle locationBundle:atom.getLocations()) {
                storageService.addLocation(atom.guid(), locationBundle);
            }

        } catch (SOSProtocolException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to replicate data to node " + node.guid().toMultiHash() + " -- Message: " + e.getMessage());
            return false;
        }

        return true;
    }

    private Atom transferDataRequest(InputStream data, Node node) throws SOSProtocolException {

        try {
            URL url = SOSURL.STORAGE_POST_ATOM(node);
            SOS_LOG.log(LEVEL.INFO, "Replicating data to " + url.toString());
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.POST, url, ResponseType.JSON);

            DataPackage dataPackage = new DataPackage();
            dataPackage.setGuid(guid.toMultiHash());
            dataPackage.setData(IO.InputStreamToBase64String(data)); // Data is transformed to base64 as expected by the REST API

            DataPackage.Metadata metadata = new DataPackage.Metadata();
            if (delegateReplication) {
                metadata.setReplicationFactor(replicationFactor - 1);

                DataPackage.Metadata.ReplicationNodes replicationNodes = new DataPackage.Metadata.ReplicationNodes();
                replicationNodes.setType(nodesCollection.type());
                replicationNodes.setRefs(nodesCollection.nodesRefs()
                        .stream()
                        .map(IKey::toMultiHash)
                        .toArray(String[]::new));

                metadata.setReplicationNodes(replicationNodes);
            }

            if (dataIsAlreadyProtected) {
                metadata.setProtectedData(true);
            }
            dataPackage.setMetadata(metadata);

            String jsonBody = JSONHelper.jsonObjMapper().writeValueAsString(dataPackage);
            request.setJSONBody(jsonBody);

            Response response = RequestsManager.getInstance().playSyncRequest(request);
            if (response instanceof ErrorResponseImpl) {
                setState(TaskState.ERROR);
                throw new IOException();
            }

            try(InputStream body = response.getBody()) {

                if (response.getCode() == HTTPStatus.CREATED) {

                    return JSONHelper.jsonObjMapper().readValue(body, AtomManifest.class);

                } else {
                    throw new SOSProtocolException("Unable to transfer create data on remote node");
                }
            }

        } catch (IOException | SOSURLException e) {
            throw new SOSProtocolException("Unable to transfer DATA", e);
        }

    }

    @Override
    public String toString() {
        return "AtomReplication. ReplicationFactor: " + replicationFactor;
    }
}
