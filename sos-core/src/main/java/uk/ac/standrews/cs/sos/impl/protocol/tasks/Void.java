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
package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;

/**
 * NOTE: use this only for testing and experiments
 * 
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
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            setState(TaskState.ERROR);
        }

        setState(TaskState.SUCCESSFUL);
        SOS_LOG.log(LEVEL.DEBUG, toString() + " Finished performing action");
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
