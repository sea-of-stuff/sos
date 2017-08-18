package uk.ac.standrews.cs.sos.experiments;

import org.testng.annotations.Test;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetricsTest {

    @Test
    public void basicDatasetMetricsTest() {

        Metrics.Dataset(new File("sos-experiments/src/main/resources/datasets/text"));
    }

    @Test
    public void largeDatasetMetricsTest() {

        Metrics.Dataset(new File("sos-experiments/src/main/resources/datasets/images"));
    }

    @Test
    public void randomDatasetMetricsTest() {

        Metrics.Dataset(new File("sos-experiments/src/main/resources/datasets/random_1"));
    }

    @Test
    public void subDirsDatasetMetricsTest() {

        Metrics.Dataset(new File("sos-experiments/src/main/resources/datasets/deep"));
    }
}
