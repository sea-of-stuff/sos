package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestCacheImpl_TIPS_HEAD_Test extends TIPS_HEAD_Test {

    @Test
    public void basicTipTest() throws Exception {
        super.basicTipTest(new ManifestsCacheImpl());
    }

    @Test
    public void advanceTipTest() throws Exception {
        super.advanceTipTest(new ManifestsCacheImpl());
    }

    @Test
    public void multipleTipsTest() throws Exception {
        super.multipleTipsTest(new ManifestsCacheImpl());
    }

    @Test
    public void advanceMultipleTipsTest() throws Exception {
        super.advanceMultipleTipsTest(new ManifestsCacheImpl());
    }

    @Test
    public void basicHeadTest() throws Exception, HEADNotFoundException {
        super.basicHeadTest(new ManifestsCacheImpl());
    }

    @Test
    public void basicMultiHeadSameVersionTest() throws Exception, HEADNotFoundException {
        super.basicMultiHeadSameVersionTest(new ManifestsCacheImpl());
    }

    @Test
    public void basicMultiHeadDifferentVersionTest() throws Exception, HEADNotFoundException {
        super.basicMultiHeadDifferentVersionTest(new ManifestsCacheImpl());
    }

    @Test
    public void noDuplicatesInTip() throws Exception, HEADNotFoundException {
        super.noDuplicatesInTip(new ManifestsCacheImpl());
    }

}
