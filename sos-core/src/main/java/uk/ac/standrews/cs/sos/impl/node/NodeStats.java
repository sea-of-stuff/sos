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
