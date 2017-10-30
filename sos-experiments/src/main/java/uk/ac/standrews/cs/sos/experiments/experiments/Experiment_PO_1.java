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

        // Prepare the experiments to be runIteration
        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            units.add(new ExperimentUnit_PO_1());
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public void finishIteration() {
        super.finishIteration();

        InstrumentFactory.instance().measure(StatsTYPE.experiment, "END OF EXPERIMENT PO_1.");
    }

    private class ExperimentUnit_PO_1 implements ExperimentUnit {

        private ContextService cms;

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"SETTING UP EXPERIMENT");
            System.out.println("node id is " + node.guid().toMultiHash());

            try {
                System.out.println("adding users/roles to node");
                addFolderUSROToNode(node, experiment);

                cms = node.getCMS();

                String datasetPath = experiment.getExperimentNode().getDatasetPath();

                System.out.println("adding content to node");
                addFolderContentToNode(node, new File(datasetPath));

                System.out.println("adding contexts to node");
                addContexts();

                System.out.println("running predicates");
                cms.runPredicates();
            } catch (Exception e) {
                throw new ExperimentException();
            }
        }

        @Override
        public void run() {
            InstrumentFactory.instance().measure(StatsTYPE.experiment,"RUNNING EXPERIMENT");

            cms.runPolicies();
        }

        private void addContexts() throws Exception {

            addContext(cms, experiment, "no_policies");
            addContext(cms, experiment, "do_nothing_policy");

            // Roles loaded from experiment resources
            addContext(cms, experiment, "grant_access");

            // Must have multiple nodes up and running
            addContext(cms, experiment, "data_replication_1");
//            addContext(cms, experiment, "Manifest_Replication_1");
        }

    }

    // REMOVEME
    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "po_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PO_1 experiment_po_1 = new Experiment_PO_1(experimentConfiguration);
        experiment_po_1.process();
    }

}
