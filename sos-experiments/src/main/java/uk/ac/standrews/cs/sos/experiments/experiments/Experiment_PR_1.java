package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PR_1 implements Experiment {

    private SOSLocalNode node;
    private long start, end, timeToFinish;

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

        addContentToNode();
        addContexts();
    }

    @Override
    public void start() {

        start = System.nanoTime();

        node.getCMS().runPredicates();
    }

    @Override
    public void finish() {
        end = System.nanoTime();
    }

    @Override
    public void collectStats() {
        timeToFinish = end - start;
        System.out.println("All predicates run in " + timeToFinish/1000000000.0 + " seconds");
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
