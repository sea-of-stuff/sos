package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Investigate the context performance as the cardinality of its domain changes
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_DO_3 extends BaseExperiment implements Experiment {

    private Iterator<ExperimentUnit> experimentUnitIterator;

    // Must be static to be initialized before constructor
    private static String[] contextsToRun = new String[] {"predicate_6"};
    private static String[] subdatasets = new String[] { "text_1kb_1", "text_100kb_1", "text_1mb_1" }; // NOTE - 1mb dataset has less than 100 files

    private final String masterDataset;

    public Experiment_DO_3(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        masterDataset = experiment.getExperimentNode().getDataset();

        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for (String aContextsToRun : contextsToRun) {
                for(String subdataset: subdatasets) {
                    units.add(new ExperimentUnit_DO_3(experiment, aContextsToRun, subdataset));
                }
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

        return experiment.getSetup().getIterations() * contextsToRun.length * subdatasets.length;
    }

    private class ExperimentUnit_DO_3 extends ExperimentUnit_DO {

        private static final int SUBSET_SIZE = 60;

        ExperimentUnit_DO_3(ExperimentConfiguration.Experiment experiment, String contextFilename, String subdataset) {
            super(TYPE.subset, experiment, contextFilename, SUBSET_SIZE, masterDataset + "/" + subdataset + "/");
        }

        @Override
        public void setup() throws ExperimentException {

            this.setLocalNode(node);
            super.setup();
        }

    }
}
