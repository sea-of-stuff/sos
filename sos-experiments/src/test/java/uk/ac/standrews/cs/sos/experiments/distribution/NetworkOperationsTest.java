package uk.ac.standrews.cs.sos.experiments.distribution;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NetworkOperationsTest {

    @Test (enabled = false)
    public void directoryTransfer() throws NetworkException {

        NetworkOperations networkOperations = new NetworkOperations();

        ExperimentConfiguration.Experiment.Node.SSH ssh = new ExperimentConfiguration.Experiment.Node.SSH();
        ssh.setType(1);
        ssh.setHost("hogun-10.cluster");
        ssh.setUser("sic2");
        ssh.setKnown_hosts("/Users/sic2/.ssh/known_hosts");
        ssh.setConfig("/Users/sic2/.ssh/config");
        ssh.setPrivateKeyPath("/Users/sic2/.ssh/id_rsa");
        ssh.setPassphrase("85GDkVfAMHAV//2ZcoTeW8YzfB1mQHdbm9A/ZdSYuRY6Va2whCYprV0Uh+Dx+ZxGnAmK5SRIzFIfPjrqyeHzYA==");

        networkOperations.setSsh(ssh);
        networkOperations.connect();

        networkOperations.sendDirectory("/Users/sic2/Downloads/Test", "/cs/scratch/sic2/data", true);
        networkOperations.disconnect();
    }

}