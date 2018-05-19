package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.sos.constants.Internals;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Testing IO on data and manifests
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_GUID_2 extends BaseExperiment implements Experiment {

    private Iterator<ExperimentUnit> experimentUnitIterator;

    public Experiment_GUID_2(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        File[] subsets = new File(experiment.getExperimentNode().getDatasetPath()).listFiles();
        assert(subsets != null);
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for (File subset : subsets) {
                units.add(new ExperimentUnit_GUID_2(subset));
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

        File[] subsets = new File(experiment.getExperimentNode().getDatasetPath()).listFiles();
        assert(subsets != null);
        return experiment.getSetup().getIterations() * subsets.length;
    }

    private class ExperimentUnit_GUID_2 implements ExperimentUnit {

        private File subset;

        public ExperimentUnit_GUID_2(File subset) {
            this.subset = subset;
        }

        @Override
        public void setup() {}

        @Override
        public void run() throws ExperimentException {
            System.out.println("Processing subset: " + subset.getAbsolutePath());

            Internals.GUID_ALGORITHM = ALGORITHM.SHA256;
            try {
                addFolderContentToNodeAsAtoms(node, subset);

            } catch (IOException e) {
                e.printStackTrace();
                throw new ExperimentException();
            }

            rest_a_bit();

            Internals.GUID_ALGORITHM = ALGORITHM.SHA1;
            try {
                addFolderContentToNodeAsAtoms(node, subset);

            } catch (IOException e) {
                e.printStackTrace();
                throw new ExperimentException();
            }

            rest_a_bit();

//            Internals.GUID_ALGORITHM = ALGORITHM.SHA384;
//            try {
//                addFolderContentToNodeAsAtoms(node, subset);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new ExperimentException();
//            }
//
//            rest_a_bit();

            Internals.GUID_ALGORITHM = ALGORITHM.SHA512;
            try {
                addFolderContentToNodeAsAtoms(node, subset);

            } catch (IOException e) {
                e.printStackTrace();
                throw new ExperimentException();
            }

            rest_a_bit();

            Internals.GUID_ALGORITHM = ALGORITHM.MD5;
            try {
                addFolderContentToNodeAsAtoms(node, subset);

            } catch (IOException e) {
                e.printStackTrace();
                throw new ExperimentException();
            }

        }
    }

    public static void main(String[] args) throws ConfigurationException, ExperimentException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "guid_2") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_GUID_2 experiment_guid_2 = new Experiment_GUID_2(experimentConfiguration, "guid_2_run_2");
        experiment_guid_2.process();
    }
}
