package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.distribution.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PR_1 extends BaseExperiment implements Experiment {

    private ContextService cms;
    private int counter;

    private Iterator<ExperimentUnit> experimentUnitIterator;
    private ExperimentUnit currentExperimentUnit;

    public Experiment_PR_1(ExperimentConfiguration experimentConfiguration) {
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

        InstrumentFactory.instance().measure(StatsTYPE.experiment, "END OF EXPERIMENT PR1. # times a predicate was run: " + counter);
    }

    @Override
    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations() * CONTEXT_TYPE.values().length;
    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "pr_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PR_1 experiment_pr_1 = new Experiment_PR_1(experimentConfiguration);
        experiment_pr_1.process();
    }

    private class ExperimentUnit_PR_1 implements ExperimentUnit {

        private CONTEXT_TYPE context_type;

        ExperimentUnit_PR_1(CONTEXT_TYPE context_type) {
            this.context_type = context_type;
        }

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"SETTING UP EXPERIMENT with context type " + context_type.name());

            // TODO - notes for the experiment
            // This experiment should be process against different types of contexts
            // contexts should be loaded from file
            // Instrumentation code will only measure the time to process the predicates, so we can ignore overhead time
            try {
                cms = node.getCMS();

                addFolderContentToNode(new File(TEST_DATA_FOLDER));
                addContexts();
            } catch (Exception e) {
                e.printStackTrace();
                throw new ExperimentException();
            }
        }

        @Override
        public void run() {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"RUNNING EXPERIMENT with context type " + context_type.name());

            counter = cms.runPredicates();
        }

        private void addFolderContentToNode(File folder) throws URISyntaxException, MetadataException, IOException {

            File[] listOfFiles = folder.listFiles();

            assert listOfFiles != null;
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {

                    String fileLocation = listOfFile.getAbsolutePath();
                    AtomBuilder atomBuilder = new AtomBuilder().setLocation(new URILocation(fileLocation));
                    Metadata metadata = node.getAgent().addMetadata(atomBuilder.getLocation().getSource()); // TODO - do this in the version builder?
                    VersionBuilder versionBuilder = new VersionBuilder()
                            .setAtomBuilder(atomBuilder)
                            .setMetadata(metadata);

                    Version version = node.getAgent().addData(versionBuilder);
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added version " + version.guid() + " from URI " + fileLocation);
                }
            }

        }

        private void addContexts() throws Exception {

            switch(context_type) {
                case ALL: {
                    IGUID c_1 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "all.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_1 " + c_1);

                    IGUID c_2 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "reject_all.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_2 " + c_2);

                    break;
                }

                case DATA: // TODO - no idea what to do here...
                case DATA_AND_METADATA: {
                    break;
                }

                case METADATA: {
                    IGUID c_1 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "is_img.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_1 " + c_1);

                    IGUID c_2 = cms.addContext(new File(CONTEXTS_FOLDER.replace("{experiment}", experiment.getName()) + "is_mp3.json"));
                    InstrumentFactory.instance().measure(StatsTYPE.experiment, "Added context c_2 " + c_2);
                    break;
                }
            }

        }

    }

    public enum CONTEXT_TYPE {
        ALL, DATA, METADATA, DATA_AND_METADATA
    }
}
