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
package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.utilities.Pair;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeStats {

    private IGUID node;
    private Queue<Pair<Long, DataPoint >> measurements;
    private double averageAvailability;

    public NodeStats(IGUID node) {
        this.node = node;
        this.measurements = new LinkedList<>();
        this.averageAvailability = 0.0;
    }

    public void addMeasure(Long timestamp, boolean available, Long latency) {

        if (measurements.size() == 0) {
            averageAvailability = available ? 1 : 0;
        } else {
            // to calculate the new average after then nth number, you multiply the old average by n−1, add the new number, and divide the total by n.
            averageAvailability = (averageAvailability * (measurements.size()) + (available ? 1 : 0)) / (measurements.size() + 1);
        }

        DataPoint dataPoint = new DataPoint();
        dataPoint.available = available;
        dataPoint.latency = latency;
        dataPoint.progressiveAvgAvailability = averageAvailability;
        measurements.add(new Pair<>(timestamp, dataPoint));
    }

    public Queue<Pair<Long, DataPoint>> getMeasurements() {
        return measurements;
    }

    public double getAverageAvailability() {
        return averageAvailability;
    }

    public static class DataPoint {

        Boolean available;
        Long latency;
        double progressiveAvgAvailability;

        public double getProgressiveAvgAvailability() {
            return progressiveAvgAvailability;
        }
    }

}
