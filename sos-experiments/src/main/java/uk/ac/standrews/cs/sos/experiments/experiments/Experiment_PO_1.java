package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The experiment PO_A_1 investigates the performance of contexts when the policies operate on data, metadata, roles, etc
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_PO_1 extends BaseExperiment implements Experiment {

    public Experiment_PO_1(ExperimentConfiguration experimentConfiguration) throws ExperimentException {
        super(experimentConfiguration);
    }

    public Experiment_PO_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return new ExperimentUnit_PO_1();
    }

    private class ExperimentUnit_PO_1 implements ExperimentUnit {

        private ContextService cms;

        private List<IGUID> tmpList;
        private IGUID tmp;

        @Override
        public void setup() throws ExperimentException {
            System.out.println("Node GUID is " + node.guid().toMultiHash());

            try {
                System.out.println("Adding users/roles to node");
                addFolderUSROToNode(node, experiment);

                cms = node.getCMS();

                System.out.println("Adding content to node");
                String datasetPath = experiment.getExperimentNode().getDatasetPath();
                Role role = node.getUSRO().getRole(GUIDFactory.recreateGUID("SHA256_16_485bc6e643077d0d825d92f883ecb7bc18f5d62242e4752dd9772f21a6886317"));
                tmpList = addFolderContentToNode(node, new File(datasetPath), role);

                System.out.println("Adding contexts to node");
                addContexts();

                System.out.println("Running Predicates");
                cms.runPredicates();
            } catch (ContextException | IOException e) {
                throw new ExperimentException(e);
            } catch (GUIDGenerationException e) {
                e.printStackTrace();
            } catch (RoleNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() throws ExperimentException {
            System.out.println("Running Policies");
            cms.runPolicies();

            rest_a_bit(2000);

            System.out.println("Running Check Policies");
            cms.runCheckPolicies();

            rest_a_bit(2000);
        }

        @Override
        public void finish() {

            try {
                if (tmp != null && tmpList != null) {
                    Context context = cms.getContext(tmp);
                    deleteData(node, context, tmpList);
                }
            } catch (ContextNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void addContexts() throws ContextException {
            // addContext(cms, experiment, "no_policies");
            addContext(cms, experiment, "do_nothing_policy");

            // Must have multiple nodes up and running
            tmp = addContext(cms, experiment, "data_replication_1");
            addContext(cms, experiment, "manifest_replication_1");

            addContext(cms, experiment, "grant_access");
        }

    }

    public static void main(String[] args) throws ExperimentException, ConfigurationException {

        File experimentConfigurationFile = new File(CONFIGURATION_FOLDER.replace("{experiment}", "po_a_1") + "configuration.json");
        ExperimentConfiguration experimentConfiguration = new ExperimentConfiguration(experimentConfigurationFile);

        Experiment_PO_1 experiment_po__1 = new Experiment_PO_1(experimentConfiguration);
        experiment_po__1.process();
    }

}
