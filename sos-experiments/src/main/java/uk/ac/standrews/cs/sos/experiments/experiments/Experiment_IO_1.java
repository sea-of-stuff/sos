package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.services.Agent;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * Testing IO on data and manifests
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_IO_1 extends BaseExperiment implements Experiment {

    public Experiment_IO_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_IO_1();
    }

    private class ExperimentUnit_IO_1 implements ExperimentUnit {

        private static final boolean INVALIDATE_CACHE = true; // NOTE

        @Override
        public void setup() {}

        @Override
        public void run() throws ExperimentException {
            List<IGUID> atoms;

            String datasetPath = experiment.getExperimentNode().getDatasetPath();
            try {
                atoms = addFolderContentToNodeAsAtoms(node, new File(datasetPath));

            } catch (IOException e) {
                e.printStackTrace();
                throw new ExperimentException();
            }

            rest_a_bit();

            if (INVALIDATE_CACHE) {
                node.getMDS().shutdown();
            }
            Agent agent = node.getAgent();

            for(IGUID atom:atoms) {
                try {
                    agent.getData(atom);
                } catch (ServiceException e) {
                    throw new ExperimentException("Unable to get data for atom with GUID " + atom.toMultiHash());
                }
            }

            rest_a_bit();
        }
    }

    public static void main(String[] args) throws ConfigurationException, ExperimentException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "io_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_IO_1 experiment_io_1 = new Experiment_IO_1(experimentConfiguration, "io_1_on_text1mb_10its_1");
        experiment_io_1.process();
    }
}
