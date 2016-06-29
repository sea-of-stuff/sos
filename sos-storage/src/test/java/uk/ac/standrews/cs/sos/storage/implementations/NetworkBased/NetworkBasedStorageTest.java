package uk.ac.standrews.cs.sos.storage.implementations.NetworkBased;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NetworkBasedStorageTest {
    @Test
    public void testGetRoot() throws Exception {

        NetworkBasedStorage storage = new NetworkBasedStorage("sic2", "public_html");

        SOSDirectory dir = storage.getRoot();
        Iterator it = dir.getIterator();
        while(it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

}