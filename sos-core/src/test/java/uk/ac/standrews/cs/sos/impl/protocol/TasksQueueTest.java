package uk.ac.standrews.cs.sos.impl.protocol;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.Void;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
            assertTrue(false);
        }

        assertEquals(voidTask.getState(), TaskState.SUCCESSFUL);
    }

    @Test
    public void timeoutSyncTest() {

        Task voidTask = new Void(35000);
        TasksQueue.instance().performSyncTask(voidTask);

        assertEquals(voidTask.getState(), TaskState.ERROR);
    }

    // FIXME
    @Test
    public void timeoutAsyncTest() {

        Task voidTask = new Void(35000);
        TasksQueue.instance().performAsyncTask(voidTask);

        // Let task finish
        try {
            Thread.sleep(45000);
        } catch (InterruptedException e) {
            assertTrue(false);
        }

        assertEquals(voidTask.getState(), TaskState.ERROR);
    }
}