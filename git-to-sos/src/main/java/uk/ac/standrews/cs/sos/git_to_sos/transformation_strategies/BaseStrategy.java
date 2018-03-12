package uk.ac.standrews.cs.sos.git_to_sos.transformation_strategies;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.DAG;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BaseStrategy {

    protected SOSLocalNode node;
    protected DAG dag;

    protected HashMap<String, IGUID> all = new LinkedHashMap<>();

    protected HashMap<String, String> blobToSOS = new LinkedHashMap<>();
    protected HashMap<String, String> treesToSOS = new LinkedHashMap<>();
    protected HashMap<String, String> commitsToSOS = new LinkedHashMap<>();

    BaseStrategy(SOSLocalNode node, DAG dag) {
        this.node = node;
        this.dag = dag;
    }
}
