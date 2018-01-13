package uk.ac.standrews.cs.sos.experiments.experiments;

import uk.ac.standrews.cs.sos.experiments.Experiment;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;
import uk.ac.standrews.cs.sos.experiments.ExperimentUnit;
import uk.ac.standrews.cs.sos.experiments.exceptions.ExperimentException;

/**
 * NB stands for Normal Behaviours.
 *
 * See this SO answer to know how to plot timelines using ggplot2
 * https://stackoverflow.com/a/9862712/2467938
 *
 * NOTE: I should not use the experiment pattern for NB experiments.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Experiment_NB_1 extends BaseExperiment implements Experiment {

    public Experiment_NB_1(ExperimentConfiguration experimentConfiguration, String outputFilename) throws ExperimentException {
        super(experimentConfiguration, outputFilename);
    }

    @Override
    public ExperimentUnit getExperimentUnit() {
        return null;
    }
}
