package uk.ac.standrews.cs.sos.git_to_sos.transformation_strategies;

import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.DAG;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AllVersioned extends BaseStrategy {

    AllVersioned(SOSLocalNode node, DAG dag) {
        super(node, dag);
    }
}
