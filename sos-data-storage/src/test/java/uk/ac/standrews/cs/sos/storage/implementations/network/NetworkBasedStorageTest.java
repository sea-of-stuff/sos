package uk.ac.standrews.cs.sos.storage.implementations.network;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NetworkBasedStorageTest {

    @Test (enabled = false)
    public void testGetRoot() throws Exception {

        NetworkBasedStorage storage = new NetworkBasedStorage("sic2", "public_html", false);

        Directory dir = storage.getRoot();
        Iterator it = dir.getIterator();
        while(it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

}