package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.FetchManifest;
import uk.ac.standrews.cs.sos.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * The remote manifest directory allows the node to replicate manifests to other nodes in the SOS
 * as well as finding manifests in the rest of the SOS
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteManifestsDirectory extends AbstractManifestsDirectory implements ManifestsDirectory {

    private DDSIndex ddsIndex;
    private NodeDiscoveryService nodeDiscoveryService;
    private DataDiscoveryService dataDiscoveryService;

    public RemoteManifestsDirectory(DDSIndex ddsIndex, NodeDiscoveryService nodeDiscoveryService, DataDiscoveryService dataDiscoveryService) {
        this.ddsIndex = ddsIndex;
        this.nodeDiscoveryService = nodeDiscoveryService;
        this.dataDiscoveryService = dataDiscoveryService;
    }

    /**
     * Async operation
     *
     * @param manifest
     * @throws ManifestPersistException
     */
    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {

        // FIXME - metadata and context should be replicated at different end-points
        // TODO - Policy based on context?

        Iterator<Node> nodes = nodeDiscoveryService.getNodes(NodeType.STORAGE).iterator();
        int replicationFactor = 1; // FIXME - do not hardcode replication-factor. use context

        try {
            ManifestReplication replicationTask = new ManifestReplication(manifest, nodes, replicationFactor, dataDiscoveryService);
            TasksQueue.instance().performAsyncTask(replicationTask);
        } catch (SOSProtocolException e) {
            throw new ManifestPersistException("Unable to persist node to remote nodes");
        }

    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

        Manifest retval = null;

        Set<IGUID> guids = ddsIndex.getDDSRefs(guid); // TODO - do not just use nodes in the index, since it may not be sufficient
        if (guids == null) {
            throw new ManifestNotFoundException("Unable to find manifest because there are no known DDS nodes");
        }

        for(IGUID g:guids) {
            try {
                Node node = nodeDiscoveryService.getNode(g);

                FetchManifest fetchManifest = new FetchManifest(node, guid); // FIXME - use different end-points for context, metadata, etc
                TasksQueue.instance().performSyncTask(fetchManifest);

                retval = fetchManifest.getManifest();
                if (retval != null) {
                    break;
                }
            } catch (NodeNotFoundException | IOException e) {
                SOS_LOG.log(LEVEL.WARN, "A problem occurred while attempting to fetch a manifest with GUID " + guid + " from Node with GUID " + g);
            }

        }

        if (retval == null) {
            throw new ManifestNotFoundException("Unable to find manifest in other known DDS nodes");
        }

        return retval;
    }

    @Override
    public void advanceTip(IGUID invariant, IGUID version) {}

    @Override
    public void advanceTip(IGUID invariant, Set<IGUID> previousVersion, IGUID newVersion) {}

    @Override
    public Set<IGUID> getTips(IGUID invariant) {
        return null;
    }

    @Override
    public IGUID getHead(IGUID invariant) {
        return null;
    }

    @Override
    public void setHead(Version version) {}

    @Override
    public void flush() {}

}
