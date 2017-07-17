package uk.ac.standrews.cs.sos.experiments.distribution;

import com.jcraft.jsch.JSchException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDistribution {

    public void distribute(ExperimentConfiguration configuration) throws NetworkException, JSchException, InterruptedException {

        for(NodeConfiguration nodeConfiguration:configuration.getNodesConfigurations()) {

            ScpViaSsh scp = new ScpViaSsh();
            scp.host = nodeConfiguration.getHost();
            scp.user = nodeConfiguration.getUser();
            scp.privateKeyPath = nodeConfiguration.getPrivateKeyPath();
            scp.passphrase = nodeConfiguration.getPassphrase();
            scp.connect();

            scp.sendFile("", "Desktop/sos.jar");
            scp.sendFile("", "Desktop/config.conf");
            scp.executeJar("Desktop/sos.jar", "-c config.conf -j -fs -root 73a7f67f31908dd0e574699f163eda2cc117f7f4");

            scp.disconnect();

            Thread.sleep(2000);
        }

    }
}
