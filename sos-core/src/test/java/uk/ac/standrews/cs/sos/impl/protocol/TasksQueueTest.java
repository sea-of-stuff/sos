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

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.Void;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TasksQueueTest extends SetUpTest {

    @Test
    public void basicSyncTest() {

        Task voidTask = new Void();
        TasksQueue.instance().performSyncTask(voidTask);

        assertEquals(voidTask.getState(), TaskState.SUCCESSFUL);
    }

    @Test
    public void multiSyncTasksTest() {

        Task voidTask = new Void();
        TasksQueue.instance().performSyncTask(voidTask);
        TasksQueue.instance().performSyncTask(voidTask);
        TasksQueue.instance().performSyncTask(voidTask);

        assertEquals(voidTask.getState(), TaskState.SUCCESSFUL);
    }

    @Test
    public void basicAsyncTest() {

        Task voidTask = new Void(5000);
        TasksQueue.instance().performAsyncTask(voidTask);

        assertEquals(voidTask.getState(), TaskState.RUNNING);

        // Let task finish
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            fail();
        }

        assertEquals(voidTask.getState(), TaskState.SUCCESSFUL);
    }

    @Test
    public void multiAsyncTasksTest() {

        Task voidTask = new Void();
        TasksQueue.instance().performAsyncTask(voidTask);
        TasksQueue.instance().performAsyncTask(voidTask);
        TasksQueue.instance().performAsyncTask(voidTask);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            fail();
        }

        assertEquals(voidTask.getState(), TaskState.SUCCESSFUL);
    }

    @Test
    public void timeoutSyncTest() {

        Task voidTask = new Void(35000);
        TasksQueue.instance().performSyncTask(voidTask);

        assertEquals(voidTask.getState(), TaskState.ERROR);
    }

    @Test
    public void timeoutAsyncTest() {

        Task voidTask = new Void(35000);
        TasksQueue.instance().performAsyncTask(voidTask);

        // Let task finish
        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            fail();
        }

        assertEquals(voidTask.getState(), TaskState.ERROR);
    }
}