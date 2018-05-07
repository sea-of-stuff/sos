package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.util.*;

/**
 * Investigate the context performance as the cardinality of its domain changes
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_DO_2 extends BaseExperiment implements Experiment {

    private Iterator<ExperimentUnit> experimentUnitIterator;

    // Must be static to be initialized before constructor
    private static String[] contextsToRun = new String[] {"predicate_1", "predicate_2", "predicate_3",
                                                            "predicate_6", "predicate_10"};
    private ArrayList<Integer> datasetSizes = new ArrayList<>();

    public Experiment_DO_2(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        for(int i = 1; i <= 10000/500; i++) {
            datasetSizes.add(i * 500);
        }

        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for (String aContextsToRun : contextsToRun) {
                for(Integer datasetSize : datasetSizes) {
                    units.add(new ExperimentUnit_DO_2(experiment, aContextsToRun, datasetSize));
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

        return experiment.getSetup().getIterations() * contextsToRun.length * datasetSizes.size();
    }

    class ExperimentUnit_DO_2 extends ExperimentUnit_DO {

        ExperimentUnit_DO_2(ExperimentConfiguration.Experiment experiment, String contextFilename, int datasetSize) {
            super(TYPE.datasetSize, experiment, contextFilename, datasetSize, "");
        }

        public void setup() throws ExperimentException {

            this.setLocalNode(node);
            super.setup();
        }
    }

}
