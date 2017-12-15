package uk.ac.standrews.cs.sos.impl.protocol;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;

import static uk.ac.standrews.cs.sos.constants.Internals.TIMEOUT_LIMIT_S;

/**
 * Singleton pattern used for this class.
 *
 * TODO - ability to persist tasks -- tasks must be "describable"
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TasksQueue {

    private SettingsConfiguration.Settings.GlobalSettings.TasksSettings settings;
    private boolean fallbackToSyncTasks;
    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService executorService;

    private static TasksQueue instance;
    private TasksQueue() {
        settings = SOSLocalNode.settings.getGlobal().getTasks();
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

        performAsyncTask(task, true, false);
    }

    public void performAsyncTask(Task task) {

        performAsyncTask(task, false, true);
    }

    private void performAsyncTask(Task task, boolean sync, boolean checkSettings) {


        if (task.getState() != TaskState.INITIALIZED) {
            SOS_LOG.log(LEVEL.ERROR, "TasksQueue :: You cannot resubmit " + task);
            return;
        }

        if (checkSettings && fallbackToSyncTasks) {
            performSyncTask(task);
        } else {

            SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Submitting " + task + " Sync: " + sync);
            persist(task);

            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(task::run, executorService);
            try {

                final CompletableFuture<Integer> responseFuture = within(future, Duration.ofSeconds(TIMEOUT_LIMIT_S));
                responseFuture
                        .exceptionally(throwable -> {
                            task.setState(TaskState.ERROR);
                            SOS_LOG.log(LEVEL.ERROR, "TasksQueue :: Error/Timeout for " + task + " Message: " + throwable.getMessage());
                            throw new CancellationException();
                        });

                if (sync) {
                    responseFuture.join();
                }

            } catch (CompletionException | CancellationException e) {
                // boolean cancelled = future.cancel(true);
                task.setState(TaskState.ERROR);
            }

            SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Finished/Submitted " + task);
        }
    }

    private <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {

        final CompletableFuture<T> timeout = failAfter(duration);
        return future.applyToEither(timeout, Function.identity()); // TODO - sync vs async
    }

    private <T> CompletableFuture<T> failAfter(Duration duration) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduledExecutorService.schedule(() -> {
            final TimeoutException ex = new TimeoutException("Timeout after " + duration.getSeconds() + " seconds");
            return promise.completeExceptionally(ex);
        }, duration.getSeconds(), TimeUnit.SECONDS);
        return promise;
    }

    private void persist(Task task) {
        // TODO - add task to db
        // SOS_LOG.log(LEVEL.INFO, "TasksQueue :: WIP - task should be persisted " + task);
    }
}
