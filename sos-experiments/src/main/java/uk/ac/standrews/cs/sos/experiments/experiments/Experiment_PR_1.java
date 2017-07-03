package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.actors.ContextServiceExperiment;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PR_1 extends BaseExperiment implements Experiment {

    private SOSLocalNode node;
    private ContextServiceExperiment cms;


    public static void main(String[] args) throws Exception {

        Experiment_PR_1 experiment_pr_1 = new Experiment_PR_1();
        experiment_pr_1.setup();

        experiment_pr_1.start();
        experiment_pr_1.finish();
        experiment_pr_1.collectStats();
    }

    @Override
    public void setup() throws Exception {
        File configFile = new File("PATH");
        SOSConfiguration configuration = new SOSConfiguration(configFile);

        node = ServerState.init(configuration);
        cms = (ContextServiceExperiment) node.getCMS();

        addContentToNode();
        addContexts();
    }

    @Override
    public void start() {
        super.start();

        cms.runPredicates();
    }

    private void addContentToNode() {

        // Add a bunch of data, versions, and so on here
    }

    private void addContexts() throws Exception {

        node.getCMS().addContext("" +
                "{\n" +
                "    \"name\": \"All\",\n" +
                "    \"predicate\": \"CommonPredicates.AcceptAll();\"\n" +
                "}");
    }
}
