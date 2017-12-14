package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Void extends Task {

    private static final int SLEEP_TIME_DEFAULT = 1000;
    private int sleepTime;

    public Void() {
        this(SLEEP_TIME_DEFAULT);
    }

    public Void(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    protected void performAction() {
        SOS_LOG.log(LEVEL.DEBUG, toString() + " Performing action. Sleeping time (ms): " + sleepTime);

        try {
            for(int i = 0; i < (sleepTime / 1000); i++) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            setState(TaskState.ERROR);
        }

        SOS_LOG.log(LEVEL.DEBUG, toString() + " Finished performing action");

        setState(TaskState.SUCCESSFUL);
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Task deserialize(String json) throws IOException {
        return null;
    }
}
