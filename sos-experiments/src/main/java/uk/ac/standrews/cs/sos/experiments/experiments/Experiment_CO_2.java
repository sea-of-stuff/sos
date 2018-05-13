package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Reduce shared code with CO_1
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_CO_2 extends BaseExperiment implements Experiment {

    private Iterator<ExperimentUnit> experimentUnitIterator;

    // Must be static to be initialized before constructor
    private static String[] contextsToRun = new String[] {"data_replication_1", "data_replication_2", "data_replication_3",
            "data_replication_4", "data_replication_5", "data_replication_6",
            "data_replication_7", "data_replication_8", "data_replication_9",
            "data_replication_10"};

    public Experiment_CO_2(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_CO_2(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for (String aContextsToRun:contextsToRun) {
                units.add(new ExperimentUnit_CO_2(aContextsToRun));
            }
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public ExperimentUnit getExperimentUnit() {

        return experimentUnitIterator.next();
    }

    @Override
    public int numberOfTotalIterations() {

        return experiment.getSetup().getIterations() * contextsToRun.length;
    }

    private class ExperimentUnit_CO_2 implements ExperimentUnit {

        private String contextName;
        private ContextService cms;


        ExperimentUnit_CO_2(String contextName) {
            this.contextName = contextName;
        }

        @Override
        public void setup() throws ExperimentException {
            System.out.println("Node GUID is " + node.guid().toMultiHash());

            try {
                cms = node.getCMS();

                System.out.println("Adding content to node");

                // NOTE - Keep amount of data fixed
                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                addFolderContentToNode(node, new File(datasetPath), -1);

                System.out.println("Add context " + contextName + " to node");
                addContext(cms, experiment, contextName);

                System.out.println("Running Predicates");
                cms.runPredicates();
            } catch (ContextException | IOException e) {
                throw new ExperimentException();
            }
        }

        @Override
        public void run() throws ExperimentException {
            System.out.println("Running Policies");
            cms.runPolicies();

            rest_a_bit(2000);

            System.out.println("Running Check Policies");
            cms.runCheckPolicies();

            rest_a_bit(2000);
        }

    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "co_a_2") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_CO_2 experiment_co__2 = new Experiment_CO_2(experimentConfiguration);
        experiment_co__2.process();
    }


}
