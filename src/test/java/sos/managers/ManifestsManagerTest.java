package sos.managers;

import constants.Hashes;
import org.json.JSONObject;
import org.testng.annotations.Test;
import sos.configurations.DefaultConfiguration;
import sos.model.implementations.components.manifests.CompoundManifest;
import sos.model.interfaces.components.Manifest;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManagerTest {

    private static final String EXPECTED_JSON_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"ManifestGUID\":\"2ffdfe2d899c4db7cde6d76cc2ade7ff49d5e0b9\"," +
                    "\"ContentGUID\":\"a412b829e2e1f4e982f4f75b99e4bbaebb73e411\"," +
                    "\"Contents\":" +
                    "[{" +
                    "\"Type\":\"label\"," +
                    "\"Value\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}]}";

    private JSONObject jsonObj = new JSONObject(EXPECTED_JSON_CONTENTS);

    @Test
    public void testAddManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(new DefaultConfiguration(), null);


        Manifest mockedManifest = mock(CompoundManifest.class);
        when(mockedManifest.isValid()).thenReturn(true);
        when(mockedManifest.toJSON()).thenReturn(jsonObj);
        manifestsManager.addManifest(mockedManifest);

        // TODO - query sea of stuff
        assertTrue(true);
    }
}