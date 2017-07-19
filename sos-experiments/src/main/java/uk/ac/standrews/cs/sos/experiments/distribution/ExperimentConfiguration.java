package uk.ac.standrews.cs.sos.experiments.distribution;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.configuration.Configuration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ExperimentConfiguration extends Configuration {

    /**
     * Create a configuration using the specified file (must be accessibly locally)
     *
     * @param file
     */
    public ExperimentConfiguration(File file) throws ConfigurationException {
        super(file);
    }

    public String getExperimentName() {
        return getString(PropertyKeys.EXPERIMENT_NAME);
    }

    public List<NodeConfiguration> getNodesConfigurations() {

        List<NodeConfiguration> retval = new LinkedList<>();

        JsonNode node = getNode(PropertyKeys.EXPERIMENT_NODES);
        for (JsonNode child : node) {

            NodeConfiguration nodeConfiguration = JSONHelper.JsonObjMapper().convertValue(child, NodeConfiguration.class);
            retval.add(nodeConfiguration);
        }

        return retval;
    }

    private class PropertyKeys {

        static final String EXPERIMENT_NAME = "experiment.name";
        static final String EXPERIMENT_SETUP_ITERATIONS = "experiment.setup.iterations";
        static final String EXPERIMENT_SETUP_APP = "experiment.setup.app";

        static final String EXPERIMENT_NODES = "experiment.nodes";

        static final String NODE_BEHAVIOUR = "behaviour";
        static final String NODE_STATS = "stats";

    }
}
