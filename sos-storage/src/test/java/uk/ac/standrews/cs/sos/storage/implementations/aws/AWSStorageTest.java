package uk.ac.standrews.cs.sos.storage.implementations.aws;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.storage.data.StringData;
import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AWSStorageTest {

    @Test
    public void dummyTest() throws PersistenceException {
        Storage storage = new AWSStorage("sos-simone-test", false);

        File file = storage.createFile(storage.getDataDirectory(), "example.txt", new StringData("hello world"));
        file.persist();
        assertTrue(file.exists());
    }
}