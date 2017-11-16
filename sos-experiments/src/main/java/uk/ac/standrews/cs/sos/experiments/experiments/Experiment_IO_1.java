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

            List<IGUID> versions;

            try {
                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                versions = addFolderContentToNode(node, new File(datasetPath));

            } catch (Exception e) {
                e.printStackTrace();
                throw new ExperimentException();
            }

            // node.getMDS().shutdown(); // TODO - comment this line out to invalidate caches
            Agent agent = node.getAgent();

            for(IGUID version:versions) {
                try {
                    agent.getData(version);
                } catch (ServiceException e) {
                    throw new ExperimentException("Unable to get data for version with GUID " + version.toMultiHash());
                }
            }

        }
    }


    public static void main(String[] args) throws ChicShockException, ConfigurationException, ExperimentException, InterruptedException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "io_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_IO_1 experiment_io_1 = new Experiment_IO_1(experimentConfiguration);
        experiment_io_1.process();
    }
}
