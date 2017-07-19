package uk.ac.standrews.cs.sos.experiments.distribution;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDistribution {

    public void distribute(ExperimentConfiguration configuration) throws NetworkException, InterruptedException {

        for(NodeConfiguration nodeConfiguration:configuration.getNodesConfigurations()) {

            NetworkOperations scp = new NetworkOperations();
            scp.host = nodeConfiguration.getHost();
            scp.user = nodeConfiguration.getUser();
            scp.privateKeyPath = nodeConfiguration.getSsh().getPrivate_key();
            scp.passphrase = nodeConfiguration.getSsh().getPassphrase();
            scp.connect();

            scp.sendFile("", "Desktop/sos.jar");
            scp.sendFile(nodeConfiguration.getConfigurationFilePath(), "Desktop/config.conf");
            scp.executeJar("Desktop/sos.jar", "-c config.conf -j -fs -root 73a7f67f31908dd0e574699f163eda2cc117f7f4");

            scp.disconnect();
        }

    }
}
