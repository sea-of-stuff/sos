package uk.ac.standrews.cs.sos.impl.protocol;

import java.io.IOException;

/**
 * This class defines the main components of a task.
 *
 * A task is a unit of work that SOS components can submit to a queue of tasks.
 * @see TasksQueue for more info on how the tasks are processed.
 *
 *
 * Task serialization/deserialization:
 * Instead of using explicit components to do this, we force the tasks to implement the abstract methods:
 * #serialize() and #deserialize(String json)
 * The reason behind this is that we want all tasks to be serializable/deserializable
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Task implements Runnable {

    /**
     * This method runs the task.
     * When the task is finished, we call notify()
     * The monitoring pattern is used to achieve sync-tasks using the same components that we use for async-tasks.
     */
    @Override
    public synchronized void run() {
        performAction();
        notify(); // Awake the SYNC tasks from their wait state
    }

    /**
     * Perform the actual task
     */
    public abstract void performAction();


    /**
     * Serialize the task to a JSON string.
     *
     * @return json string
     */
    public abstract String serialize();

    /**
     * Deserialize a task from a JSON string.
     * @param json string representation of the task
     * @return the task
     */
    public abstract Task deserialize(String json) throws IOException;
}
