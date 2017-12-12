package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

import java.io.File;

/**
 * Investigate the context performance as the cardinality of its domain changes
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_DO_1 extends BaseExperiment implements Experiment {

    public Experiment_DO_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_DO_1();
    }

    @Override
    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations();
    }

    private class ExperimentUnit_DO_1 implements ExperimentUnit {

        ExperimentUnit_DO_1() {

        }

        @Override
        public void setup() throws ExperimentException {

            // data to add to nodes
            // datasets should be split as evenly as possible across nodes
        }

        @Override
        public void run() {

            long start = System.nanoTime();
            // Run predicate locally and trigger remote nodes
            // wait for all nodes (included local node) to return
            long duration = System.nanoTime() - start;
            InstrumentFactory.instance().measure(StatsTYPE.predicate, StatsTYPE.predicate_dataset, "TODO - NUMBER OF NODES", duration);
        }

    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "do_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_DO_1 experiment_do_1 = new Experiment_DO_1(experimentConfiguration);
        experiment_do_1.process();
    }


}
