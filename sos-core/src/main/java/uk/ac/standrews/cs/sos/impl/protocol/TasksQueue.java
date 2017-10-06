package uk.ac.standrews.cs.sos.impl.protocol;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton pattern used for this class.
 *
 * TODO - ability to persist tasks -- tasks must be "describable"
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TasksQueue {

    private static final int TIMEOUT_LIMIT_S = 30;
    private ScheduledExecutorService executorService;

    private static TasksQueue instance;
    private TasksQueue() {
        SettingsConfiguration.Settings.ThreadSettings threadSettings = SOSLocalNode.settings.getGlobal().getTasks().getThread();
        executorService = Executors.newScheduledThreadPool(threadSettings.getPs());

        // TODO - load tasks from db
        // for each task, submit it to the executorService
    }

    public static TasksQueue instance() {
        if (instance == null) {
            instance = new TasksQueue();
        }

        return instance;
    }

    public void performSyncTask(Task task) {

        try {
            synchronized (task) {
                performAsyncTask(task);

                task.wait();
                SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Task finished " + task);
            }
        } catch (InterruptedException e) {
            SOS_LOG.log(LEVEL.ERROR, "TasksQueue :: " + e.getMessage());
        }

    }

    public void performAsyncTask(Task task) {

        SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Submitting task " + task);
        persist(task);

        final Future handler = executorService.submit(task);
        executorService.schedule(() -> {
            handler.cancel(true);
            SOS_LOG.log(LEVEL.WARN, "TasksQueue :: Cancelled task " + task);

            task.notify();
        }, TIMEOUT_LIMIT_S, TimeUnit.SECONDS);

        SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Task submitted " + task);
    }

    private void persist(Task task) {
        // TODO - add task to db
        SOS_LOG.log(LEVEL.INFO, "TasksQueue :: WIP - task should be persisted " + task);
    }
}
