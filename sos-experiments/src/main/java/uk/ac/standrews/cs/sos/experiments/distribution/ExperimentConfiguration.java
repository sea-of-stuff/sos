package uk.ac.standrews.cs.sos.experiments.distribution;

import uk.ac.standrews.cs.sos.configuration.Configuration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;

import java.io.File;
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

    public List<NodeConfiguration> getNodesConfigurations() {
        return null;
    }

    private class PropertyKeys {

        static final String EXPERIMENT_NAME = "experiment.name";
        static final String EXPERIMENT_SETUP_ITERATIONS = "experiment.setup.iterations";
        static final String EXPERIMENT_SETUP_APP = "experiment.setup.app";

        static final String EXPERIMENT_NODES = "experiment.nodes";
        static final String NODE_ID = "id";
        static final String NODE_HOST = "host";
        static final String NODE_USER = "user";
        static final String NODE_SSH_KEY = "ssh.key";
        static final String NODE_PASSPHRASE = "ssh.passphrase";
        static final String NODE_CONFIG = "configuration";

        static final String NODE_BEHAVIOUR = "behaviour";
        static final String NODE_STATS = "stats";

    }
}
