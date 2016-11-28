package uk.ac.standrews.cs.sos.model.manifests.directory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.actors.protocol.SOSEP;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotSetException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

/**
 * The remote manifest directory allows the node to replicate manifests to other nodes in the SOS
 * as well as finding manifests in the rest of the SOS
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteManifestsDirectory implements ManifestsDirectory {

    private ManifestPolicy manifestPolicy;
    private LocalNodesDirectory localNodesDirectory;

    public RemoteManifestsDirectory(ManifestPolicy manifestPolicy, LocalNodesDirectory localNodesDirectory) {
        this.manifestPolicy = manifestPolicy;
        this.localNodesDirectory = localNodesDirectory;
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {

        int replicationFactor = manifestPolicy.getReplicationFactor();
        if (replicationFactor > 0) {

            Set<Node> ddsNodes = localNodesDirectory.getDDSNodes();

            for(Node ddsNode:ddsNodes) {
                SOS_LOG.log(LEVEL.INFO, "Attempting to replicate manifest " + manifest.getContentGUID() +
                        " to node " + ddsNode.getNodeGUID().toString());
                addManifest(ddsNode, manifest);
            }

        }
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

        // Contact knwon DDS nodes
        // Ask such nodes about manifest with given guid

        throw new ManifestNotFoundException("remote directory - findManifest not implemented yet");
    }

    @Override
    public Asset getHEAD(IGUID invariant) throws HEADNotFoundException {
        throw new NotImplementedException();
    }

    @Override
    public void setHEAD(IGUID version) throws HEADNotSetException {
        throw new NotImplementedException();
    }

    @Override
    public void flush() {

    }

    private void addManifest(Node node, Manifest manifest) {

        try {
            URL url = SOSEP.DDS_POST_MANIFEST(node);

            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setJSONBody(manifest.toString());

            Response response = RequestsManager.getInstance().playSyncRequest(request);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
