package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ServerState;
import uk.ac.standrews.cs.sos.experiments.distribution.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.Instrument;
import uk.ac.standrews.cs.sos.instrument.MeasureTYPE;
import uk.ac.standrews.cs.sos.services.experiments.ContextServiceExperiment;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PR_1 extends BaseExperiment implements Experiment {

    private SOSLocalNode node;
    private ContextServiceExperiment cms;

    private int counter;

    public Experiment_PR_1(ExperimentConfiguration experimentConfiguration) {
        super(experimentConfiguration);
    }

    @Override
    public void setup() throws ExperimentException {

        try {
            Instrument.instance(MeasureTYPE.CSV, OUTPUT_FOLDER + "pr1.out");

            File configFile = new File(CONFIGURATION_FOLDER + "pr_1/node_0.json");
            SettingsConfiguration configuration = new SettingsConfiguration(configFile);

            node = ServerState.init(configuration.getSettingsObj());
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

        try {
            Instrument.instance().measure("END OF EXPERIMENT PR1");
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER + "pr_1/configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PR_1 experiment_pr_1 = new Experiment_PR_1(experimentConfiguration);
        experiment_pr_1.run();
    }
}
