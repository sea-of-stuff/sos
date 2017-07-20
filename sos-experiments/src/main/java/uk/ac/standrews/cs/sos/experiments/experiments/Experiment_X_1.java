package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.actors.experiments.ContextServiceExperiment;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.*;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

import java.io.File;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_X_1 extends BaseExperiment implements Experiment {

    private SOSLocalNode node;
    private ContextServiceExperiment cms;

    private int counter;

    @Override
    public void setup() throws ExperimentException {

        try {
            File configFile = new File(CONFIGURATION_FOLDER + "x_1/x_1.json");
            SOSConfiguration configuration = new SOSConfiguration(configFile);

            node = ServerState.init(configuration);
            cms = (ContextServiceExperiment) node.getCMS();

            addContentToNode();
            addContexts();
        } catch (Exception e) {
            throw new ExperimentException();
        }
    }

    @Override
    public void start() {
        super.start();

        counter = cms.runPredicates();
    }

    @Override
    public void finish() {
        super.finish();

        ServerState.kill();
    }

    @Override
    public void collectStats() {
        super.collectStats();

        System.out.println("Number of entities processed by the predicate: " + counter);
    }

    private void addContentToNode() throws URISyntaxException {

        // Add a bunch of data, versions, and so on here
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(new URILocation("https://www.takemefishing.org/tmf/assets/images/fish/american-shad-464x170.png"));
        VersionBuilder versionBuilder = new VersionBuilder().setAtomBuilder(atomBuilder);

        node.getAgent().addData(versionBuilder);
    }

    private void addContexts() throws Exception {

        node.getCMS().addContext("" +
                "{\n" +
                "    \"name\": \"All\",\n" +
                "    \"predicate\": \"CommonPredicates.AcceptAll();\"\n" +
                "}");
    }

    public static void main(String[] args) throws ChicShockException, ConfigurationException, ExperimentException {

        ChicShock chicShock = new ChicShock(CONFIGURATION_FOLDER + "pr_1/configuration.json");
        chicShock.chic();

        Experiment_X_1 experiment_pr_1 = new Experiment_X_1();
        experiment_pr_1.run();

        chicShock.unShock();
    }
}
