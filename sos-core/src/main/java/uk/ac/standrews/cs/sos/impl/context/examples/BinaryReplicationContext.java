package uk.ac.standrews.cs.sos.impl.context.examples;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.impl.context.BaseContext;
import uk.ac.standrews.cs.sos.impl.context.PolicyLanguage;
import uk.ac.standrews.cs.sos.impl.context.SOSPredicateImpl;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Iterator;

/**
 * This is a context that replicates all binary content to at least three randomly chosen nodes
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BinaryReplicationContext extends BaseContext {

    private static final int NUMBER_OF_REPLICAS = 3;

    public BinaryReplicationContext(String name, NodesCollection domain, NodesCollection codomain) {
        super(name, domain, codomain);
    }

    // FIXME
    public BinaryReplicationContext(IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {
        super(guid, name, domain, codomain);
    }

    @Override
    public SOSPredicate predicate() {

        return new SOSPredicateImpl(guid -> {

            SOSAgent agent = SOSAgent.instance();

            try {
                String contentType = getMetaProperty(agent, guid, MetadataConstants.CONTENT_TYPE);
                return isOctetStream(contentType);

            } catch (Exception e) {
                // This could occur because the metadata could not be found or the type property was not available
                SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
            }

            return false;
        }, Long.MAX_VALUE);
    }

    private boolean isOctetStream(String contentType) {

        return contentType.equals("application/octet-stream");
    }

    @Override
    public Policy[] policies() {
        return new Policy[]{
                new ManifestReplicationPolicy(NUMBER_OF_REPLICAS)
        };
    }

    /**
     * Replicate manifests at least n-times
     */
    private class ManifestReplicationPolicy implements Policy {

        private int factor;

        ManifestReplicationPolicy(int factor) {
            this.factor = factor;
        }

        // TODO - rename to apply()
        @Override
        public boolean run(Manifest manifest) {

            try {
                Iterator<Node> nodes = PolicyLanguage.instance().getNodes(null, NodeType.DDS).iterator();
                PolicyLanguage.instance().replicateManifest(manifest, nodes, factor);

                return true;
            } catch (SOSException e) {
                e.printStackTrace();
            }

            return false;
        }

        // TODO - rename to satisfied()
        @Override
        public boolean check(Manifest manifest) {

            try {
                int numberReplicas = PolicyLanguage.instance().numberOfReplicas(null, manifest.guid());
                return numberReplicas >= factor;

            } catch (SOSException e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
