package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_CO_C_1 extends BaseExperiment implements Experiment {

    public Experiment_CO_C_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_CO_C_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_CO_C_1();
    }

    private class ExperimentUnit_CO_C_1 implements ExperimentUnit {

        private ContextService cms;

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "SETTING UP EXPERIMENT");
            System.out.println("Node GUID is " + node.guid().toMultiHash());

            try {
                cms = node.getCMS();

                System.out.println("Adding content to node");

                // NOTE - Keep amount of data fixed
                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                addFolderContentToNode(node, new File(datasetPath));

                System.out.println("Adding contexts to node");
                addContexts();

                System.out.println("Running Predicates");
                cms.runPredicates();

                System.out.println("Running Policies");
                cms.runPolicies();
            } catch (ContextException | IOException e) {
                throw new ExperimentException();
            }
        }

        @Override
        public void run() {

            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "RUNNING EXPERIMENT");

            System.out.println("Running CHECK Policies");
            cms.runCheckPolicies();
        }

        private void addContexts() throws ContextException {

            addContext(cms, experiment, "do_nothing_policy");
            addContext(cms, experiment, "data_replication_1");
            addContext(cms, experiment, "data_replication_2");
            addContext(cms, experiment, "data_replication_3");
            addContext(cms, experiment, "data_replication_4");
            addContext(cms, experiment, "data_replication_5");
            addContext(cms, experiment, "data_replication_6");
            addContext(cms, experiment, "data_replication_7");
            addContext(cms, experiment, "data_replication_8");
            addContext(cms, experiment, "data_replication_9");
            addContext(cms, experiment, "data_replication_10");
        }

    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "co_c_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_CO_C_1 experiment_co_c_1 = new Experiment_CO_C_1(experimentConfiguration);
        experiment_co_c_1.process();
    }


}
