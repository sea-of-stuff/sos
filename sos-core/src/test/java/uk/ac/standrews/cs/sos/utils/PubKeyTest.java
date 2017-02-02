package uk.ac.standrews.cs.sos.utils;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PubKeyTest {

    @Test
    public void dummy() throws KeyGenerationException, EncryptionException {
        PubKey p = new PubKey();
        p.generateKeys();

        String encrypted = p.encrypt64("\n" +
                "02-02-2017 09:14:44 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53450652 [Thread-1] ) ==> ===============================================================\n" +
                "02-02-2017 09:14:44 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53450652 [Thread-1] ) ==> File /argon/data/marcus/status/status.html.tmp was modified\n" +
                "02-02-2017 09:14:44 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53450652 [Thread-1] ) ==> File /argon/data/marcus/status/status.html.tmp won't be tracked\n" +
                "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53774958 [Thread-1] ) ==> ===============================================================\n" +
                "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53774958 [Thread-1] ) ==> File /argon/data/marcus/status/status.html was modified\n" +
                "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.FileListener  ( 53774958 [Thread-1] ) ==> Starting to track file /argon/data/marcus/status/status.html\n" +
                "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.parsers.STAParser  ( 53775046 [Thread-1] ) ==> Starting to read Setup Table entries\n" +
                "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.parsers.STAParser  ( 53775046 [Thread-1] ) ==> Finished to read Setup Table entries\n" +
                "02-02-2017 09:20:09 [ INFO  ] -  uk.ac.nomad.tracker.parsers.STAParser  ( 53775046 [Thread-1] ) ==> Sta");
        System.out.println(encrypted);
    }
}