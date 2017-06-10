package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.manifest.CURRENTNotFoundException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestCacheImpl_HEADS_CURRENT_Test extends HEADS_CURRENT_Test {

    @Test
    public void basicHeadTest() throws Exception {
        super.basicHeadTest(new ManifestsCacheImpl());
    }

    @Test
    public void advanceHeadTest() throws Exception {
        super.advanceHeadTest(new ManifestsCacheImpl());
    }

    @Test
    public void multipleHeadsTest() throws Exception {
        super.multipleHeadsTest(new ManifestsCacheImpl());
    }

    @Test
    public void advanceMultipleHeadsTest() throws Exception {
        super.advanceMultipleHeadsTest(new ManifestsCacheImpl());
    }

    @Test
    public void basicCurrentTest() throws Exception, CURRENTNotFoundException {
        super.basicCurrentTest(new ManifestsCacheImpl());
    }

    @Test
    public void basicMultiCurrentSameVersionTest() throws Exception, CURRENTNotFoundException {
        super.basicMultiCurrentSameVersionTest(new ManifestsCacheImpl());
    }

    @Test
    public void basicMultiCurrentDifferentVersionTest() throws Exception, CURRENTNotFoundException {
        super.basicMultiCurrentDifferentVersionTest(new ManifestsCacheImpl());
    }

}
