package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PR_1 extends BaseExperiment implements Experiment {

    private int counter;

    public Experiment_PR_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);

        // Prepare the experiments to be run
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for(int j = 0; j < CONTEXT_TYPE.values().length; j++) {

                CONTEXT_TYPE context_type = CONTEXT_TYPE.values()[j];
                units.add(new ExperimentUnit_PR_1(context_type));
            }
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public void finish() {
        super.finish();

        InstrumentFactory.instance().measure(StatsTYPE.experiment, "END OF EXPERIMENT PR_1. # times a predicate was run: " + counter);
    }

    @Override
    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations() * CONTEXT_TYPE.values().length;
    }

    public enum CONTEXT_TYPE {
        ALL, DATA, METADATA, DATA_AND_METADATA, MANIFEST
    }

    private class ExperimentUnit_PR_1 implements ExperimentUnit {

        private ContextService cms;
        private CONTEXT_TYPE context_type;

        ExperimentUnit_PR_1(CONTEXT_TYPE context_type) {
            this.context_type = context_type;
        }

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"SETTING UP EXPERIMENT with context type " + context_type.name());

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
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"RUNNING EXPERIMENT with context type " + context_type.name());

            counter = cms.runPredicates();
        }

        private void addContexts() throws Exception {

            switch(context_type) {
                case ALL: {
                    IGUID c_1 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "all.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_1 " + c_1.toShortString());

                    IGUID c_2 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "reject_all.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_2 " + c_2.toShortString());

                    break;
                }

                case DATA: {
                    IGUID c_1 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "search_word_the.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_1 " + c_1.toShortString());

                    IGUID c_2 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "occurrence_word_the.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_2 " + c_2.toShortString());

                    IGUID c_3 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "occurrence_word_alice.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_3 " + c_3.toShortString());

                    IGUID c_4 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "search_sentence_online_marketing.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_4 " + c_4.toShortString());

                    break;
                }
                case DATA_AND_METADATA: {
                    IGUID c_1 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "occurrence_word_the_on_text_only.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_1 " + c_1.toShortString());

                    break;
                }

                case METADATA: {
                    IGUID c_1 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "is_img.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_1 " + c_1.toShortString());

                    IGUID c_2 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "is_mp3.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_2 " + c_2.toShortString());

                    IGUID c_3 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "is_text.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_3 " + c_3.toShortString());

                    IGUID c_4 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "is_pdf.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_4 " + c_4.toShortString());

                    IGUID c_5 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "greater_than_100_kb.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_5 " + c_5.toShortString());
                    break;
                }

                case MANIFEST: {

                    IGUID c_1 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "content_protected.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_1 " + c_1.toShortString());

                    IGUID c_2 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "content_not_protected.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_2 " + c_2.toShortString());
                }
            }

        }

    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException, IOException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "pr_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PR_1 experiment_pr_1 = new Experiment_PR_1(experimentConfiguration);
        experiment_pr_1.process();
    }
}
