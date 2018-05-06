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

    private Context context;
    private int datasetSize;
    protected String contextFilename;
    protected List<IGUID> allVersions;

    private SOSLocalNode node;
    private ContextService cms;
    private ExperimentConfiguration.Experiment experiment;

    public ExperimentUnit_DO(SOSLocalNode node, ExperimentConfiguration.Experiment experiment, String contextFilename, int datasetSize) {
        this.node = node;
        this.experiment = experiment;
        this.contextFilename = contextFilename;
        this.datasetSize = datasetSize;
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

            System.out.println("Adding content to nodes");
            allVersions = distributeData(experiment, node, context, datasetSize);

        } catch (ManifestPersistException | ContextException | IOException e) {
            throw new ExperimentException();
        }
    }

    @Override
    public void run() throws ExperimentException {

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(11); // 11 threads should be enough

            List<Callable<Object>> runnables = new LinkedList<>();
            runnables.add(triggerLocalPredicate());
            runnables.addAll(triggerRemotePredicate(context));

            System.out.println("Running Predicates across domain of size: " + context.domain(false).size());
            long start = System.nanoTime();
            executorService.invokeAll(runnables); // This method returns when all the calls finish
            long duration = System.nanoTime() - start;
            InstrumentFactory.instance().measure(StatsTYPE.predicate_remote, StatsTYPE.predicate_dataset, contextFilename, duration);

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
