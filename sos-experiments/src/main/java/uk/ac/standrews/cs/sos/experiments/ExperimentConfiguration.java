package uk.ac.standrews.cs.sos.experiments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.instrument.impl.Statistics;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static uk.ac.standrews.cs.sos.experiments.experiments.BaseExperiment.CONFIGURATION_FOLDER;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ExperimentConfiguration {

    @JsonIgnore
    private JsonNode node;

    public ExperimentConfiguration() {}

    /**
     * Create a configuration using the specified file (must be accessibly locally)
     *
     * @param file
     */
    public ExperimentConfiguration(File file) throws ConfigurationException {
        try {
            node = JSONHelper.JsonObjMapper().readTree(file);
        } catch (IOException e) {
            throw new ConfigurationException("Unable to read configuration properly");
        }
    }

    /**
     * Utility method to get the Experiment object
     * @return
     */
    public Experiment getExperimentObj() {

        return JSONHelper.JsonObjMapper().convertValue(node, ExperimentConfiguration.class).getExperiment();
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
    public static class Experiment {

        protected String name;
        private String description;
        private Setup setup;
        private List<Node> nodes;
        private Node experimentNode;
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

        public Node getExperimentNode() {
            return experimentNode;
        }

        public void setExperimentNode(Node experimentNode) {
            this.experimentNode = experimentNode;
        }

        public Statistics getStats() {
            return stats;
        }

        public void setStats(Statistics stats) {
            this.stats = stats;
        }

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

        public static class Node {

            private int id;
            private boolean remote;
            private String configurationFile;
            private SSH ssh;
            private Behaviour behaviour;

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

            public String getConfigurationFile(String experimentName) {
                return CONFIGURATION_FOLDER.replace("{experiment}", experimentName) + configurationFile;
            }

            public void setConfigurationFile(String configurationFile) {
                this.configurationFile = configurationFile;
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

            public static class SSH {
                private int type;
                private String host;
                private String user;
                private String privateKeyPath;
                private String passphrase;
                private String known_hosts;
                private String config;
                private String password;

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
                    return passphrase;
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
                    return password;
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

    }


}
