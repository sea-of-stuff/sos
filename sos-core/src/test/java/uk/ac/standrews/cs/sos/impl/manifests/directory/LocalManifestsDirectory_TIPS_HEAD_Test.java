package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;

import java.lang.reflect.Method;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalManifestsDirectory_TIPS_HEAD_Test extends TIPS_HEAD_Test {

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
    public void basicTipTest() throws Exception {
        super.basicTipTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void advanceTipTest() throws Exception {
        super.advanceTipTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void multipleTipsTest() throws Exception {
        super.multipleTipsTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void advanceMultipleTipsTest() throws Exception {
        super.advanceMultipleTipsTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void basicHeadTest() throws Exception, HEADNotFoundException {
        super.basicHeadTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void basicOnlyOneHeadSameVersionTest() throws Exception, HEADNotFoundException {
        super.basicOnlyOneHeadSameVersionTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void basicMultiHeadDifferentVersionTest() throws Exception, HEADNotFoundException {
        super.basicMultiHeadDifferentVersionTest(new LocalManifestsDirectory(storage));
    }

    @Test
    public void noDuplicatesInTip() throws Exception, HEADNotFoundException {
        super.noDuplicatesInTip(new LocalManifestsDirectory(storage));
    }

}