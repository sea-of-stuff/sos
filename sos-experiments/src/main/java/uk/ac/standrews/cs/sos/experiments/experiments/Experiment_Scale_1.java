package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.ChicShock;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO - change experiments as in PR_1, where we use ExperimentUnits and load contexts and contents from files
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_Scale_1 extends BaseExperiment implements Experiment {

    private ContextService cms;
    private int counter;

    public Experiment_Scale_1(ExperimentConfiguration experimentConfiguration) {
        super(experimentConfiguration);

        // Prepare the experiments to be run
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            units.add(new Experiment_Scale_1.ExperimentUnit_Scale_1());
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
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
    public void run() throws ExperimentException {
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
        return experiment.getSetup().getIterations();
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

    private class ExperimentUnit_Scale_1 implements ExperimentUnit {

        @Override
        public void setup() throws ExperimentException {

        }

        @Override
        public void run() throws ExperimentException {

        }
    }

    public static void main(String[] args) throws ChicShockException, ConfigurationException, ExperimentException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "scale_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.chic();
        chicShock.shock();

        Experiment_Scale_1 experiment_pr_1 = new Experiment_Scale_1(experimentConfiguration);
        experiment_pr_1.process();

        chicShock.unShock();
        chicShock.unChic();
    }
}
