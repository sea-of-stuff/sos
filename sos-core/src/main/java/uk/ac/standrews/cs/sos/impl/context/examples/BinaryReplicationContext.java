package uk.ac.standrews.cs.sos.impl.context.examples;

import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.impl.context.BaseContext;
import uk.ac.standrews.cs.sos.impl.context.PolicyLanguage;
import uk.ac.standrews.cs.sos.impl.context.SOSPredicateImpl;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;

import java.util.Collections;
import java.util.Iterator;

/**
 * This is a context that replicates all binary content to at least three randomly chosen nodes
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BinaryReplicationContext extends BaseContext {

    private static final int NUMBER_OF_REPLICAS = 3;

    public BinaryReplicationContext(PolicyLanguage policyLanguage, String name, NodesCollection domain, NodesCollection codomain) {
        super(policyLanguage, name, domain, codomain);
    }

    @Override
    public SOSPredicate predicate() {

        return new SOSPredicateImpl(
                contentTypePredicate(Collections.singletonList("application/octet-stream")),
                Long.MAX_VALUE);
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

        @Override
        public void apply(Manifest manifest) throws PolicyException {

            Iterator<Node> nodes = policyLanguage.getNodes(null, NodeType.DDS).iterator();
            policyLanguage.replicateManifest(manifest, nodes, factor);
        }


        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {

            int numberReplicas = policyLanguage.numberOfReplicas(null, manifest.guid());
            return numberReplicas >= factor;
        }
    }
}
