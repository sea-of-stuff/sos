package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.ChicShock;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ServerState;
import uk.ac.standrews.cs.sos.experiments.distribution.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.exceptions.ChicShockException;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_X_1 extends BaseExperiment implements Experiment {

    private ContextService cms;
    private int counter;

    public Experiment_X_1(ExperimentConfiguration experimentConfiguration) {
        super(experimentConfiguration);
    }

    @Override
    public void setup() throws ExperimentException {

        try {
            cms = node.getCMS();

            addContentToNode();
            addContexts();
        } catch (Exception e) {
            throw new ExperimentException();
        }
    }

    @Override
    public void run() {
        super.run();

        counter = cms.runPredicates();
    }

    @Override
    public void finish() {
        super.finish();

        System.out.println("Number of entities processed by the predicate: " + counter);

        ServerState.kill();
    }

    @Override
    public int numberOfTotalIterations() {
        return 0; // FIXME
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

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "x_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.chic();

        Experiment_X_1 experiment_pr_1 = new Experiment_X_1(experimentConfiguration);
        experiment_pr_1.process();

        chicShock.unShock();
        chicShock.unChic();
    }
}
