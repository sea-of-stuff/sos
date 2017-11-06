package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Role;
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
public class Experiment_PO_2 extends BaseExperiment implements Experiment {

    public Experiment_PO_2(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);

        // Prepare the experiments to be runIteration
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            units.add(new ExperimentUnit_PO_2());
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public void finishIteration() {
        super.finishIteration();

        InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "END OF EXPERIMENT PO_2.");
    }

    private class ExperimentUnit_PO_2 implements ExperimentUnit {

        private ContextService cms;

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "SETTING UP EXPERIMENT");
            System.out.println("Node GUID is " + node.guid().toMultiHash());

            try {
                System.out.println("Adding users/roles to node");
                addFolderUSROToNode(node, experiment);

                cms = node.getCMS();

                String datasetPath = experiment.getExperimentNode().getDatasetPath();

                System.out.println("Adding content to node");
                Role role = node.getUSRO().getRole(GUIDFactory.recreateGUID("SHA256_16_485bc6e643077d0d825d92f883ecb7bc18f5d62242e4752dd9772f21a6886317"));
                addFolderContentToNode(node, new File(datasetPath), role);

                System.out.println("Adding contexts to node");
                addContexts();

                System.out.println("Running predicates");
                cms.runPredicates();
            } catch (Exception e) {
                throw new ExperimentException();
            }
        }

        @Override
        public void run() {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "RUNNING EXPERIMENT");

            cms.runPolicies();
        }

        private void addContexts() throws Exception {

            // Will apply the role granting policies in cascade (e.g. the grantee becomes granters on the next subpolicy)
            addContext(cms, experiment, "no_policies");
            addContext(cms, experiment, "one_policy_local");
            addContext(cms, experiment, "two_policies_local");
            addContext(cms, experiment, "three_policies_local");
            addContext(cms, experiment, "four_policies_local");
            addContext(cms, experiment, "five_policies_local");
            addContext(cms, experiment, "six_policies_local");
            addContext(cms, experiment, "seven_policies_local");
            addContext(cms, experiment, "eight_policies_local");
            addContext(cms, experiment, "nine_policies_local");
            addContext(cms, experiment, "ten_policies_local");
        }

    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "po_2") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PO_2 experiment_po_2 = new Experiment_PO_2(experimentConfiguration);
        experiment_po_2.process();
    }

}
