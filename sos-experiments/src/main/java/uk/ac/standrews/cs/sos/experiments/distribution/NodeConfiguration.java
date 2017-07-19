package uk.ac.standrews.cs.sos.experiments.distribution;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeConfiguration {

    private int id;
    private String host;
    private String user;
    private SSH ssh;

    private String configurationFilePath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public SSH getSsh() {
        return ssh;
    }

    public void setSsh(SSH ssh) {
        this.ssh = ssh;
    }

    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    public void setConfigurationFilePath(String configurationFilePath) {
        this.configurationFilePath = configurationFilePath;
    }



    public class SSH {
        private String private_key; // TODO - rename to privateKeyPath
        private String passphrase;
        private String known_hosts;

        public String getPrivate_key() {
            return private_key;
        }

        public void setPrivate_key(String private_key) {
            this.private_key = private_key;
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
    }
}
