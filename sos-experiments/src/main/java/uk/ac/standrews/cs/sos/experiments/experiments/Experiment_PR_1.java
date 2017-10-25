package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.experiments.ChicShock;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ChicShockException;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.services.ContextService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PR_1 extends BaseExperiment implements Experiment {

    private int counter;

    public Experiment_PR_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);

        // Prepare the experiments to be runIteration
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
//            for(int j = 0; j < PREDICATE_TYPE.values().length; j++) {
//
//                PREDICATE_TYPE predicate_type = PREDICATE_TYPE.values()[j];
            units.add(new ExperimentUnit_PR_1());
//            }
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public void finishIteration() {
        super.finishIteration();

        InstrumentFactory.instance().measure(StatsTYPE.experiment, "END OF EXPERIMENT PR_1. # times a predicate was runIteration: " + counter);
    }

    @Override
    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations(); //  * PREDICATE_TYPE.values().length;
    }

    public enum PREDICATE_TYPE {
        ALL, DATA, METADATA, DATA_AND_METADATA, MANIFEST
    }

    private class ExperimentUnit_PR_1 implements ExperimentUnit {

        private ContextService cms;

        ExperimentUnit_PR_1() { }

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"SETTING UP EXPERIMENT with predicate");

            try {
                cms = node.getCMS();

                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                addFolderContentToNode(node, new File(datasetPath));
                addContexts();
            } catch (Exception e) {
                throw new ExperimentException();
            }
        }

        @Override
        public void run() {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"RUNNING EXPERIMENT with predicate");

            try {
                counter = cms.runPredicates();
            } catch (ContextException e) {
                SOS_LOG.log(LEVEL.ERROR, "Experiment PR_1 - Unable to runIteration predicates properly");
            }
        }

        private void addContexts() throws Exception {


            addContext(cms, experiment, "all");
//            addContext(cms, experiment, "reject_all");


            addContext(cms, experiment, "search_word_the");
//            addContext(cms, experiment, "search_word_squirrel");
//            addContext(cms, experiment, "search_word_white_rabbit");
            addContext(cms, experiment, "occurrence_word_the");
//            addContext(cms, experiment, "occurrence_word_alice");
//            addContext(cms, experiment, "occurrence_word_bob");
//            addContext(cms, experiment, "search_sentence_online_marketing");


            addContext(cms, experiment, "search_word_the_on_text_only");
//            addContext(cms, experiment, "search_word_alice_on_text_only");
//            addContext(cms, experiment, "search_word_white_rabbit_on_text_only");


            addContext(cms, experiment, "is_img");
//            addContext(cms, experiment, "is_mp3");
//            addContext(cms, experiment, "is_text");
//            addContext(cms, experiment, "is_pdf");
//            addContext(cms, experiment, "is_png");
//            addContext(cms, experiment, "is_jpeg");
//            addContext(cms, experiment, "greater_than_100_kb");


            addContext(cms, experiment, "content_protected");
//            addContext(cms, experiment, "content_not_protected");

        }

    }

    public static void main(String[] args) throws ChicShockException, ConfigurationException, ExperimentException, InterruptedException {

        System.out.println("L/l for Local runIteration and H/h for hogun cluster runIteration");

        Scanner in = new Scanner(System.in);
        String option = in.nextLine();
        switch(option) {
            case "l": case "L":
                runLocal();
                break;

            case "h": case "H":
                runHogun();
                break;
        }
    }

    private static void runLocal() throws ConfigurationException, ExperimentException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "pr_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PR_1 experiment_pr_1 = new Experiment_PR_1(experimentConfiguration);
        experiment_pr_1.process();
    }

    private static void runHogun() throws ConfigurationException, ChicShockException, InterruptedException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "pr_1") + "configuration-hogun.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        ChicShock chicShock = new ChicShock(experimentConfiguration);
        chicShock.chicExperiment();
        chicShock.shockExperiment();

        Thread.sleep(60000);

        chicShock.unShockExperiment();
        chicShock.unChicExperiment();

    }

}
