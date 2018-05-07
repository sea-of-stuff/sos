package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;
import uk.ac.standrews.cs.sos.experiments.protocol.TriggerPredicate;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.services.ContextService;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ExperimentUnit_DO implements ExperimentUnit {

    public enum TYPE {contextName, datasetSize, subset};

    private TYPE type;
    private ExperimentConfiguration.Experiment experiment;
    private int datasetSize;
    private SOSLocalNode node;
    private String subset = "";
    private String contextFilename;
    private List<IGUID> allVersions;

    Context context;
    ContextService cms;

    ExperimentUnit_DO(TYPE type, ExperimentConfiguration.Experiment experiment, String contextFilename, int datasetSize, String subset) {
        this.type = type;
        this.experiment = experiment;
        this.contextFilename = contextFilename;
        this.datasetSize = datasetSize;
        this.subset = subset;
    }

    void setLocalNode(SOSLocalNode node) {
        this.node = node;
    }

    @Override
    public void setup() throws ExperimentException {
        System.out.println("Node GUID is " + node.guid().toMultiHash());

        try {
            cms = node.getCMS();

            System.out.println("Adding contexts to node");
            IGUID contextGUID = addContext(cms, experiment, contextFilename);

            System.out.println("Spawning context to nodes in domain. Context GUID: " + contextGUID.toMultiHash());
            context = cms.getContext(contextGUID);
            cms.spawnContext(context);

            System.out.println("Adding content to nodes");
            if (type == TYPE.subset) {
                experiment.getExperimentNode().setDataset(subset);
                System.out.println("Adding content to nodes. Subdataset: " + subset + "   --- Path: " + experiment.getExperimentNode().getDatasetPath());
            }

            allVersions = distributeData(experiment, node, context, datasetSize);

        } catch (ManifestPersistException | ContextException | IOException e) {
            throw new ExperimentException();
        }
    }

    @Override
    public void run() throws ExperimentException {
        System.out.println("Domain size: " + context.domain(false) + " (local node included)");
        System.out.println("Size dataset: " + datasetSize + " (-1 means that all the dataset will be evaluated)");

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(11); // 11 threads should be enough

            List<Callable<Object>> runnables = new LinkedList<>();
            runnables.add(triggerLocalPredicate());
            runnables.addAll(triggerRemotePredicate(context));

            System.out.println("Running Predicates across domain of size: " + context.domain(false).size());
            long start = System.nanoTime();
            executorService.invokeAll(runnables); // This method returns when all the calls finish
            long duration = System.nanoTime() - start;
            InstrumentFactory.instance().measure(StatsTYPE.predicate_remote, StatsTYPE.predicate_dataset, contextFilename, subset, datasetSize, duration);

            executorService.shutdownNow();

        } catch (InterruptedException e) {
            throw new ExperimentException(e);
        }

    }


    @Override
    public void finish() {

        // Remove data from remote nodes
        deleteData(node, context, allVersions);

        // Remove context and context results from remote nodes
        deleteContext(node, context);
    }

    private Callable<Object> triggerLocalPredicate() {

        return () -> {
            int numberOfAssets = cms.runPredicates();
            // System.out.println("Local predicate run over " + numberOfAssets + " assets"); // TODO - COMMENTME - this can affect the results
            return 0;
        };
    }

    private List<Callable<Object>> triggerRemotePredicate(Context context) {

        List<Callable<Object>> runnables = new LinkedList<>();
        NodesCollection domain = context.domain(true);
        for(IGUID nodeRef:domain.nodesRefs()) {
            runnables.add(triggerRemotePredicate(nodeRef, context.guid()));
        }

        return runnables;
    }

    private Callable<Object> triggerRemotePredicate(IGUID nodeRef, IGUID contextGUID) {

        return () -> {
            // System.out.println("Running pred for node " + nodeRef.toMultiHash()); // TODO - COMMENTME - this can affect the results
            Node nodeToContext = node.getNDS().getNode(nodeRef);
            TriggerPredicate triggerPredicate = new TriggerPredicate(nodeToContext, contextGUID);

            TasksQueue.instance().performSyncTask(triggerPredicate);

            // System.out.println("Finished to run pred for node " + nodeRef.toMultiHash() + " State: " + triggerPredicate.getState().name()); // TODO - COMMENTME - this can affect the results
            return 0;
        };
    }
}
