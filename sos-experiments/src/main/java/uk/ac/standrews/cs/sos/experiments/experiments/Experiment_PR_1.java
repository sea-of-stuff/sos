package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PR_1 extends BaseExperiment implements Experiment {

    private int counter;

    public Experiment_PR_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_PR_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_PR_1();
    }

    @Override
    public void finishIteration() {
        super.finishIteration();

        InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "END OF EXPERIMENT PR_1. # times a predicate was runIteration: " + counter);
    }

    private class ExperimentUnit_PR_1 implements ExperimentUnit {

        private ContextService cms;

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "SETTING UP EXPERIMENT");

            try {
                cms = node.getCMS();

                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                addFolderContentToNode(node, new File(datasetPath));
                addContexts();
            } catch (ContextException | IOException e) {
                e.printStackTrace();
                throw new ExperimentException();
            }
        }

        @Override
        public void run() {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "RUNNING EXPERIMENT");

            try {
                counter = cms.runPredicates();
            } catch (ContextException e) {
                SOS_LOG.log(LEVEL.ERROR, "Experiment PR_1 - Unable to runIteration predicates properly");
            }
        }

        private void addContexts() throws ContextException {

            addContext(cms, experiment, "base");

            // Data only
            addContext(cms, experiment, "common_word_occurs_once"); // the
            addContext(cms, experiment, "uncommon_word_occurs_once"); // grain
            addContext(cms, experiment, "common_word_occurs_at_least_10_times"); // the

            // Metadata and Data
            addContext(cms, experiment, "meta_common_word_occurs_once"); // the
            addContext(cms, experiment, "meta_uncommon_word_occurs_once"); // grain
            addContext(cms, experiment, "meta_common_word_occurs_at_least_10_times"); // the

            // Check one metadata property
            addContext(cms, experiment, "metadata");

            // Check two metadata features
            addContext(cms, experiment, "multi_metadata");

            // Manifest
            addContext(cms, experiment, "manifest");

        }

    }

    public static void main(String[] args) throws ConfigurationException, ExperimentException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "pr_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PR_1 experiment_pr_1 = new Experiment_PR_1(experimentConfiguration, "test_pr_1_5");
        experiment_pr_1.process();
    }


}
