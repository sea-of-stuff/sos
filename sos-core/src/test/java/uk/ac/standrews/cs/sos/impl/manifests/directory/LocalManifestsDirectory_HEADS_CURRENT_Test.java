package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.exceptions.manifest.CURRENTNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;

import java.lang.reflect.Method;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalManifestsDirectory_HEADS_CURRENT_Test extends HEADS_CURRENT_Test {

    protected LocalStorage storage;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);
        storage = new LocalStorage(stor);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        storage.destroy();
    }

    @Test
    public void basicHeadTest() throws Exception {
        super.basicHeadTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void advanceHeadTest() throws Exception {
        super.advanceHeadTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void multipleHeadsTest() throws Exception {
        super.multipleHeadsTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void advanceMultipleHeadsTest() throws Exception {
        super.advanceMultipleHeadsTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void basicCurrentTest() throws Exception, CURRENTNotFoundException {
        super.basicCurrentTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void basicMultiCurrentSameVersionTest() throws Exception, CURRENTNotFoundException {
        super.basicMultiCurrentSameVersionTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void basicMultiCurrentDifferentVersionTest() throws Exception, CURRENTNotFoundException {
        super.basicMultiCurrentDifferentVersionTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void noDuplicatesInHead() throws Exception, CURRENTNotFoundException {
        super.noDuplicatesInHead(new LocalManifestsDirectory(storage));
    }

}