package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The experiment PO 1 investigates the performance of contexts when the policies operate on data, metadata, roles, etc
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PO_1 extends BaseExperiment implements Experiment {

    public Experiment_PO_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);

        // Prepare the experiments to be run
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for(int j = 0; j < POLICY_TYPE.values().length; j++) {

                POLICY_TYPE predicate_type = POLICY_TYPE.values()[j];
                units.add(new ExperimentUnit_PO_1(predicate_type));
            }
        }

        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public void finish() {
        super.finish();

        InstrumentFactory.instance().measure(StatsTYPE.experiment, "END OF EXPERIMENT PO_1.");
    }

    @Override
    public int numberOfTotalIterations() {
        return experiment.getSetup().getIterations() * POLICY_TYPE.values().length;
    }

    public enum POLICY_TYPE {
        NONE, DATA, METADATA, MANIFEST, ROLES, NODES
    }


    private class ExperimentUnit_PO_1 implements ExperimentUnit {

        private ContextService cms;
        private POLICY_TYPE policy_type;

        ExperimentUnit_PO_1(POLICY_TYPE policy_type) {
            this.policy_type = policy_type;
        }

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"SETTING UP EXPERIMENT with policy type " + policy_type.name());

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
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"RUNNING EXPERIMENT policy type " + policy_type.name());

            cms.runPolicies();
        }

        private void addContexts() throws Exception {

            switch (policy_type) {
                case NONE:
                    // no policy in context
                    break;
                case DATA:
                    // policy with data replication?
                    break;
                case METADATA:
                    break;
            }
        }

    }

    // TODO - this experiment will require multiple nodes. It is pointless to have this experiment run locally only.
    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "po_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PO_1 experiment_pr_1 = new Experiment_PO_1(experimentConfiguration);
        experiment_pr_1.process();
    }

}
