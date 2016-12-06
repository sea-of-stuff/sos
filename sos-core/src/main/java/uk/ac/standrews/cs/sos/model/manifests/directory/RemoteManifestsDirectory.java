package uk.ac.standrews.cs.sos.model.manifests.directory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.actors.protocol.ManifestReplication;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotSetException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Set;

/**
 * The remote manifest directory allows the node to replicate manifests to other nodes in the SOS
 * as well as finding manifests in the rest of the SOS
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteManifestsDirectory implements ManifestsDirectory {

    private ManifestPolicy manifestPolicy;
    private NDS nds;
    private DDSIndex ddsIndex;

    public RemoteManifestsDirectory(ManifestPolicy manifestPolicy, NDS nds, DDSIndex ddsIndex) {
        this.manifestPolicy = manifestPolicy;
        this.nds = nds;
        this.ddsIndex = ddsIndex;
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {

        int replicationFactor = manifestPolicy.getReplicationFactor();
        if (replicationFactor > 0) {
            Set<Node> ddsNodes = nds.getDDSNodes(replicationFactor);

            for(Node ddsNode:ddsNodes) {
                SOS_LOG.log(LEVEL.INFO, "Attempting to replicate manifest " + manifest.getContentGUID() +
                        " to node " + ddsNode.getNodeGUID().toString());

                boolean replicationIsSuccessful = ManifestReplication.TransferManifestRequest(manifest, ddsNode);
                if (replicationIsSuccessful) {
                    SOS_LOG.log(LEVEL.WARN, "ddsindex is " + ddsIndex);
                    ddsIndex.addEntry(manifest.guid(), ddsNode.getNodeGUID());
                }
            }
        }

    }

    @Override
    public void addManifestDDSMapping(IGUID manifestGUID, IGUID ddsNodeGUID) {
        throw new NotImplementedException();
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

        // See if we know dds nodes already (using ddsIndex)

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
    public void flush() {}

}
