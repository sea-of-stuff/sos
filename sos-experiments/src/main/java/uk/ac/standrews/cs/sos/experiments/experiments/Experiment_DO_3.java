package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Investigate the context performance as the cardinality of its domain changes
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_DO_3 extends BaseExperiment implements Experiment {

    private Iterator<ExperimentUnit> experimentUnitIterator;

    // Must be static to be initialized before constructor
    private static String[] contextsToRun = new String[] {"predicate_6"};
    private static String[] subdatasets = new String[] { "text_1kb_1", "text_100kb_1", "text_1mb_1" }; // NOTE - 1mb dataset has less than 100 files

    private final String masterDataset;

    public Experiment_DO_3(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);

        masterDataset = experiment.getExperimentNode().getDataset();

        List<ExperimentUnit> units = new LinkedList<>();
        for(int i = 0; i < experiment.getSetup().getIterations(); i++) {
            for (String aContextsToRun : contextsToRun) {
                for(String subdataset: subdatasets) {
                    units.add(new ExperimentUnit_DO_3(node, experiment, aContextsToRun, subdataset));
                }
            }
        }
        Collections.shuffle(units);

        experimentUnitIterator = units.iterator();
    }

    @Override
    public ExperimentUnit getExperimentUnit() {

        return experimentUnitIterator.next();
    }

    @Override
    public int numberOfTotalIterations() {

        return experiment.getSetup().getIterations() * contextsToRun.length * subdatasets.length;
    }

    private class ExperimentUnit_DO_3 extends ExperimentUnit_DO {

        private String subdataset;
        private Context context;

        private ContextService cms;

        ExperimentUnit_DO_3(SOSLocalNode node, ExperimentConfiguration.Experiment experiment, String contextFilename, String subdataset) {
            super(node, experiment, contextFilename, 60);
            this.subdataset = subdataset;
        }

        @Override
        public void setup() throws ExperimentException {
            InstrumentFactory.instance().measure(StatsTYPE.experiment, StatsTYPE.none, "SETTING UP EXPERIMENT");
            System.out.println("Node GUID is " + node.guid().toMultiHash());

            try {
                cms = node.getCMS();

                System.out.println("Adding contexts to node");
                IGUID contextGUID = addContext(cms, experiment, contextFilename);

                System.out.println("Spawning context to nodes in domain. Context GUID: " + contextGUID.toMultiHash());
                context = cms.getContext(contextGUID);
                cms.spawnContext(context);

                experiment.getExperimentNode().setDataset(masterDataset + "/" + subdataset + "/");
                System.out.println("Adding content to nodes. Subdataset: " + subdataset + "   --- Path: " + experiment.getExperimentNode().getDatasetPath());
                allVersions = distributeData(experiment, node, context, 60);

            } catch (ManifestPersistException | ContextException | IOException e) {
                throw new ExperimentException(e);
            }
        }

    }
}
