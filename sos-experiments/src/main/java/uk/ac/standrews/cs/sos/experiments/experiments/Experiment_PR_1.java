package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ServerState;
import uk.ac.standrews.cs.sos.experiments.distribution.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PR_1 extends BaseExperiment implements Experiment {

    private ContextService cms;
    private int counter;

    public Experiment_PR_1(ExperimentConfiguration experimentConfiguration) {
        super(experimentConfiguration);
    }

    @Override
    public void setup() throws ExperimentException {
        super.setup();

        // TODO - notes for the experiment
        // This experiment should be run against different types of contexts
        // contexts should be loaded from file
        // Instrumentation code will only measure the time to run the predicates, so we can ignore overhead time
        try {
            cms = node.getCMS();

            addContentToNode();
            addContexts();
        } catch (Exception e) {
            e.printStackTrace();
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

        InstrumentFactory.instance().measure("END OF EXPERIMENT PR1");

        System.out.println("Number of entities processed by the predicate: " + counter);
    }

    private void addContentToNode() throws URISyntaxException, MetadataException, IOException {

        // Add a bunch of data, versions, and so on here
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(new URILocation("https://www.takemefishing.org/tmf/assets/images/fish/american-shad-464x170.png"));
        Metadata metadata = node.getAgent().addMetadata(atomBuilder.getLocation().getSource()); // TODO - do this in the version builder?
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder)
                .setMetadata(metadata);

        node.getAgent().addData(versionBuilder);
    }

    private void addContexts() throws Exception {

        IGUID c_1 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "context_1.json"));
        InstrumentFactory.instance().measure("Added context c_1 " + c_1);

        IGUID c_2 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "context_2.json"));
        InstrumentFactory.instance().measure("Added context c_2 " + c_2);

        IGUID c_5 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "context_5.json"));
        InstrumentFactory.instance().measure("Added context c_5 " + c_5);
    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "pr_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PR_1 experiment_pr_1 = new Experiment_PR_1(experimentConfiguration);
        experiment_pr_1.run();
    }
}
