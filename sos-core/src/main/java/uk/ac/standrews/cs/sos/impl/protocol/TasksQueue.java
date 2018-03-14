/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.protocol;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.*;

import static uk.ac.standrews.cs.sos.constants.Internals.TIMEOUT_LIMIT_S;

/**
 * Singleton pattern used for this class.
 *
 * TODO - ability to persist tasks -- tasks must be "describable"
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TasksQueue {


    private ExecutorService service;
    private ScheduledExecutorService canceller; // Will schedule jobs to delete
    private final Set<Task> submittedToService;

    private boolean fallbackToSyncTasks;

    private static TasksQueue instance;
    private TasksQueue() {
        SettingsConfiguration.Settings.GlobalSettings.TasksSettings settings = SOSLocalNode.settings.getGlobal().getTasks();
        fallbackToSyncTasks = settings.isFallbackToSyncTasks();

        int numberOfThreads = settings.getThread().getPs();
        this.service = Executors.newFixedThreadPool(numberOfThreads);
        this.canceller = Executors.newSingleThreadScheduledExecutor();

        this.submittedToService = new LinkedHashSet<>();
        // TODO - load tasks from db
        // for each task, submit it to the executorService
    }

    public synchronized static TasksQueue instance() {
        if (instance == null) {
            instance = new TasksQueue();
        }

        return instance;
    }

    public synchronized void shutdown() {

        service.shutdownNow();
        canceller.shutdownNow();

        instance = null;
    }

    public synchronized void performSyncTask(final Task task) {

        if (task.getState() != TaskState.INITIALIZED) {
            SOS_LOG.log(LEVEL.ERROR, "TasksQueue :: You cannot resubmit " + task);
            return;

        }

        boolean added = submittedToService.add(task);
        if (!added) {
            SOS_LOG.log(LEVEL.ERROR, "TasksQueue :: You cannot resubmit " + task);

        } else {

            SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Submitting task " + task);
            persist(task);

            Future future = service.submit(task);
            try {
                future.get(TIMEOUT_LIMIT_S, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                task.setState(TaskState.ERROR);
                SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Timeout " + task);
            }
            SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Task finished " + task);
        }

    }

    public synchronized void performAsyncTask(Task task) {

        if (fallbackToSyncTasks) {

            performSyncTask(task);

        } else {

            if (task.getState() != TaskState.INITIALIZED) {
                SOS_LOG.log(LEVEL.ERROR, "TasksQueue :: You cannot resubmit " + task);
                return;
            }

            boolean added = submittedToService.add(task);
            if (!added) {
                SOS_LOG.log(LEVEL.ERROR, "TasksQueue :: You cannot resubmit " + task);
                return;
            }

            SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Submitting task " + task);
            persist(task);

            final Future future = service.submit(task);
            canceller.schedule(() -> {
                future.cancel(true);
                task.setState(TaskState.ERROR);
                submittedToService.remove(task);

                SOS_LOG.log(LEVEL.WARN, "TasksQueue :: Cancelled task " + task);
            }, TIMEOUT_LIMIT_S, TimeUnit.SECONDS);

            SOS_LOG.log(LEVEL.INFO, "TasksQueue :: Task submitted " + task);
        }
    }

    private void persist(Task task) {
        // TODO - add task to db
        SOS_LOG.log(LEVEL.INFO, "TasksQueue :: WIP - task should be persisted " + task);
    }
}