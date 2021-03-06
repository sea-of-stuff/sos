package uk.ac.standrews.cs.sos.experiments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.instrument.impl.Statistics;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static uk.ac.standrews.cs.sos.experiments.experiments.BaseExperiment.CONFIGURATION_FOLDER;
import static uk.ac.standrews.cs.sos.experiments.experiments.BaseExperiment.USRO_FOLDER;
import static uk.ac.standrews.cs.sos.experiments.utilities.KeyGenerator.pass;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ExperimentConfiguration {

    public static final String REPO_DATASETS_PATH = "experiments/datasets/";
    public static final String REMOTE_DATASETS_PATH = "experiments/datasets/";
    public static final String REPO_CONTEXTS_PATH = "experiments/contexts/";
    public static final String REMOTE_CONTEXTS_PATH = "experiments/contexts/";
    public static final String REPO_USRO_PATH = "experiments/usro/";
    public static final String REMOTE_USRO_PATH = "experiments/usro/";

    @JsonIgnore
    private JsonNode node;

    public ExperimentConfiguration() {}

    /**
     * Create a configuration using the specified file (must be accessibly locally)
     *
     * @param file with the experiment configuration
     */
    public ExperimentConfiguration(File file) throws ConfigurationException {
        try {
            node = JSONHelper.jsonObjMapper().readTree(file);
        } catch (IOException e) {
            throw new ConfigurationException("Unable to read configuration properly", e);
        }
    }

    /**
     * Utility method to get the Experiment object
     * @return object with all the experiment configuration
     */
    public Experiment getExperimentObj() {

        return JSONHelper.jsonObjMapper().convertValue(node, ExperimentConfiguration.class).getExperiment();
    }

    @Override
    public String toString() {
        try {
            return JSONHelper.jsonObjMapper().writeValueAsString(node);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }

    }

    // POJO field for JACKSON
    private Experiment experiment;

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    ///////////////////////////////////////
    // POJO for Jackson serialisation /////
    ///////////////////////////////////////
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class Experiment {

        private String name;
        private String experimentClass;
        private String description;
        private Setup setup;
        private List<Node> nodes;
        private ExperimentNode experimentNode;
        private Statistics stats;

        public Experiment() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Setup getSetup() {
            return setup;
        }

        public void setSetup(Setup setup) {
            this.setup = setup;
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

        public ExperimentNode getExperimentNode() {
            return experimentNode;
        }

        public void setExperimentNode(ExperimentNode experimentNode) {
            this.experimentNode = experimentNode;
        }

        public Statistics getStats() {
            return stats;
        }

        public void setStats(Statistics stats) {
            this.stats = stats;
        }

        public String getExperimentClass() {
            return experimentClass;
        }

        public void setExperimentClass(String experimentClass) {
            this.experimentClass = experimentClass;
        }

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public static class Setup {

            private String app;
            private int iterations;

            public Setup() {}

            public String getApp() {
                return app;
            }

            public void setApp(String app) {
                this.app = app;
            }

            public int getIterations() {
                return iterations;
            }

            public void setIterations(int iterations) {
                this.iterations = iterations;
            }
        }

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public static class Node {

            private int id;
            private String name;
            private String path = "";
            private boolean remote;
            private String java = "java";
            private String configurationFile;
            private String certificateFile;
            private String keyFile;
            private SSH ssh;
            private Behaviour behaviour;
            private String dataset;
            // TODO - ratio of dataset to be sent (how much of the dataset to send)
            // TODO - random or sequential (-1 for random, 0, 1, 2 to indicate which part of the dataset)
            private boolean sendUSRO = false;

            @Override
            public String toString() {
                return id + "_" + name + "_" + ssh.toString();
            }

            public Node() {}

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public boolean isRemote() {
                return remote;
            }

            public void setRemote(boolean remote) {
                this.remote = remote;
            }

            public String getConfigurationFile() {
                return configurationFile;
            }

            public String getConfigurationFile(String experimentName) {
                return CONFIGURATION_FOLDER.replace("{experiment}", experimentName) + configurationFile;
            }

            public void setConfigurationFile(String configurationFile) {
                this.configurationFile = configurationFile;
            }

            public String getCertificateFile() {
                return certificateFile;
            }

            public String getCertificateFile(String experimentName) {
                return CONFIGURATION_FOLDER.replace("{experiment}", experimentName) + certificateFile;
            }

            public void setCertificateFile(String certificateFile) {
                this.certificateFile = certificateFile;
            }

            public String getKeyFile() {
                return keyFile;
            }

            public String getKeyFile(String experimentName) {
                return CONFIGURATION_FOLDER.replace("{experiment}", experimentName) + keyFile;
            }

            public void setKeyFile(String keyFile) {
                this.keyFile = keyFile;
            }

            public SSH getSsh() {
                return ssh;
            }

            public void setSsh(SSH ssh) {
                this.ssh = ssh;
            }

            public Behaviour getBehaviour() {
                return behaviour;
            }

            public void setBehaviour(Behaviour behaviour) {
                this.behaviour = behaviour;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getJava() {
                return java;
            }

            public void setJava(String java) {
                this.java = java;
            }

            public boolean hasDataset() {
                return dataset != null && !dataset.isEmpty();
            }

            public String getDataset() {
                return dataset;
            }

            public String getDatasetPath() {

                if (isRemote()) {
                    return REMOTE_DATASETS_PATH + dataset;
                } else {
                    return REPO_DATASETS_PATH + dataset;
                }
            }

            public void setDataset(String dataset) {
                this.dataset = dataset;
            }

            public String getUsro(String experimentName) {
                return USRO_FOLDER.replace("{experiment}", experimentName);
            }


            public String getContextsPath() {

                if (isRemote()) {
                    return REMOTE_CONTEXTS_PATH;
                } else {
                    return REPO_CONTEXTS_PATH;
                }
            }

            public String getUsroPath() {

                if (isRemote()) {
                    return REMOTE_USRO_PATH;
                } else {
                    return REPO_USRO_PATH;
                }
            }

            public boolean isSendUSRO() {
                return sendUSRO;
            }

            public void setSendUSRO(boolean sendUSRO) {
                this.sendUSRO = sendUSRO;
            }

            @JsonInclude(JsonInclude.Include.NON_DEFAULT)
            public static class SSH {
                private int type;
                private String host;
                private String user;
                private String privateKeyPath;
                private String passphrase;
                private String known_hosts;
                private String config;
                private String password;

                @Override
                public String toString() {
                    return type + "_" + user + "_" + host;
                }

                public SSH() {}

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public String getHost() {
                    return host;
                }

                public void setHost(String host) {
                    this.host = host;
                }

                public String getUser() {
                    return user;
                }

                public void setUser(String user) {
                    this.user = user;
                }

                public String getPrivateKeyPath() {
                    return privateKeyPath;
                }

                public void setPrivateKeyPath(String privateKeyPath) {
                    this.privateKeyPath = privateKeyPath;
                }

                public String getPassphrase() {

                    try {
                        return pass(passphrase);
                    } catch (CryptoException | FileNotFoundException e) {
                        return "INVALID";
                    }
                }

                public void setPassphrase(String passphrase) {
                    this.passphrase = passphrase;
                }

                public String getKnown_hosts() {
                    return known_hosts;
                }

                public void setKnown_hosts(String known_hosts) {
                    this.known_hosts = known_hosts;
                }

                public String getPassword() {

                    try {
                        return pass(password);
                    } catch (CryptoException | FileNotFoundException e) {
                        return "INVALID";
                    }
                }

                public void setPassword(String password) {
                    this.password = password;
                }

                public String getConfig() {
                    return config;
                }

                public void setConfig(String config) {
                    this.config = config;
                }
            }

            @JsonInclude(JsonInclude.Include.NON_DEFAULT)
            public static class Behaviour {

                private long start;
                private long stop;
                private List<Long> pause;
                private List<String> operations;

                public Behaviour() {}

                public long getStart() {
                    return start;
                }

                public void setStart(long start) {
                    this.start = start;
                }

                public long getStop() {
                    return stop;
                }

                public void setStop(long stop) {
                    this.stop = stop;
                }

                public List<Long> getPause() {
                    return pause;
                }

                public void setPause(List<Long> pause) {
                    this.pause = pause;
                }

                public List<String> getOperations() {
                    return operations;
                }

                public void setOperations(List<String> operations) {
                    this.operations = operations;
                }
            }
        }

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public static class ExperimentNode extends Node {

            // Experiments supporting these params:
            // DO_1
            //
            // Distribution is applied over nodes of the domain of contexts
            // If true, then the 'distribution_sets' param is ignored.
            private boolean equal_distribution_dataset = true;
            private int[][] distribution_sets = new int[][]{
                    // Default distribution. Assuming one node (local one).
                    new int[]{1, 100}
            };

            public boolean isEqual_distribution_dataset() {
                return equal_distribution_dataset;
            }

            public void setEqual_distribution_dataset(boolean equal_distribution_dataset) {
                this.equal_distribution_dataset = equal_distribution_dataset;
            }

            public int[][] getDistribution_sets() {
                return distribution_sets;
            }

            public void setDistribution_sets(int[][] distribution_sets) {
                this.distribution_sets = distribution_sets;
            }
        }

    }

}
