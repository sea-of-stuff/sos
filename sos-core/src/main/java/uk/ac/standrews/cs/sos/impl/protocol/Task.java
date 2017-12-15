package uk.ac.standrews.cs.sos.impl.protocol;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.security.SecureRandom;

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
public abstract class Task {

    private final long id;
    private TaskState state;

    public Task() {
        state = TaskState.INITIALIZED;
        id = new SecureRandom().nextLong();
    }

    /**
     * This method runs the task.
     * When the task is finished, we call notify()
     * The monitoring pattern is used to achieve sync-tasks using the same components that we use for async-tasks.
     */
    public int run() {
        state = TaskState.RUNNING;
        performAction();

        SOS_LOG.log(LEVEL.INFO,"Finishing Task with state " + state.name());
        return 0;
    }

    /**
     * Perform the actual task
     */
    protected abstract void performAction();


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

    /**
     * Unique random id for Task
     * @return id of task
     */
    public long getId() {
        return id;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public TaskState getState() {
        return state;
    }

    public String toString() {
        return "Task-ID: " + getId();
    }
}
