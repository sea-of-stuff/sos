package uk.ac.standrews.cs.sos.impl.protocol;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Singleton pattern used for this class.
 *
 * TODO - ability to persist tasks -- tasks must be "describable"
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TasksQueue {

    private static final int TIMEOUT_LIMIT_S = 30;
    private SettingsConfiguration.Settings.GlobalSettings.TasksSettings settings;
    private int timeout_limit;
    private boolean fallbackToSyncTasks;
    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService executorService;

    private static TasksQueue instance;
    private TasksQueue() {
        settings = SOSLocalNode.settings.getGlobal().getTasks();
        // REMOVEME - timeout_limit = settings.getTimeout_limit() >= 0 ? settings.getTimeout_limit() : TIMEOUT_LIMIT_S;
        fallbackToSyncTasks = settings.isFallbackToSyncTasks();

        int numberOfThreads = settings.getThread().getPs();
        scheduledExecutorService = Executors.newScheduledThreadPool(1); // NOTE - Not sure if core pool size of 1 is enough
        executorService = Executors.newFixedThreadPool(numberOfThreads);

        // TODO - load tasks from db
        // for each task, submit it to the scheduledExecutorService
    }

    public synchronized static TasksQueue instance() {
        if (instance == null) {
            instance = new TasksQueue();
        }

        return instance;
    }

    public void performSyncTask(final Task task) {

        try {
            synchronized (task) {
                performAsyncTask(task, false);

                while(task.getState() == TaskState.INITIALIZED) {
                    task.wait();
                }
                SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Task finished " + task.getId());
            }
        } catch (InterruptedException e) {
            SOS_LOG.log(LEVEL.ERROR, "TasksQueue :: " + e.getMessage());
        }

    }

    public void performAsyncTask(Task task) {

        performAsyncTask(task, true);
    }

    private void performAsyncTask(Task task, boolean checkSettings) {

        if (checkSettings && fallbackToSyncTasks) {
            performSyncTask(task);
        } else {

            SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Submitting task " + task.getId());
            persist(task);

            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                task.run();
                task.notify();
                return 0;
            }, executorService);

            final CompletableFuture<Integer> responseFuture = within(future, Duration.ofSeconds(TIMEOUT_LIMIT_S));
            responseFuture
                    .exceptionally(throwable -> {
                        SOS_LOG.log(LEVEL.ERROR, "TasksQueue :: Error/Timeout for task: " + task.getId() + throwable.getMessage());
                        task.notify();
                        return -1;
                    });

            SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Task submitted " + task.getId());
        }
    }

    private <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {
        final CompletableFuture<T> timeout = failAfter(duration);
        return future.applyToEither(timeout, Function.identity());
    }

    private <T> CompletableFuture<T> failAfter(Duration duration) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduledExecutorService.schedule(() -> {
            final TimeoutException ex = new TimeoutException("Timeout after " + duration);
            return promise.completeExceptionally(ex);
        }, duration.getSeconds(), TimeUnit.SECONDS);
        return promise;
    }

    private void persist(Task task) {
        // TODO - add task to db
        // SOS_LOG.log(LEVEL.INFO, "TasksQueue :: WIP - task should be persisted " + task);
    }
}
