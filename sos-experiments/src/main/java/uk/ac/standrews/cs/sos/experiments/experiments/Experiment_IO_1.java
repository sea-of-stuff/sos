package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ChicShockException;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.services.Agent;

import java.io.File;
import java.util.List;

/**
 *
 * Testing IO on data and manifests
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_IO_1 extends BaseExperiment implements Experiment {

    public Experiment_IO_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_IO_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_IO_1();
    }

    private class ExperimentUnit_IO_1 implements ExperimentUnit {

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "SETTING UP EXPERIMENT");
        }

        @Override
        public void run() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "RUNNING EXPERIMENT");

            List<IGUID> atoms;

            try {
                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                atoms = addFolderContentToNodeAsAtoms(node, new File(datasetPath));

            } catch (Exception e) {
                e.printStackTrace();
                throw new ExperimentException();
            }

            // TODO - comment this line out to invalidate caches
            // node.getMDS().shutdown();
            Agent agent = node.getAgent();

            for(IGUID atom:atoms) {
                try {
                    agent.getData(atom);
                } catch (ServiceException e) {
                    throw new ExperimentException("Unable to get data for atom with GUID " + atom.toMultiHash());
                }
            }

        }
    }


    public static void main(String[] args) throws ChicShockException, ConfigurationException, ExperimentException, InterruptedException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "io_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_IO_1 experiment_io_1 = new Experiment_IO_1(experimentConfiguration, "test_io_1_on_1000x1mb");
        experiment_io_1.process();
    }
}
