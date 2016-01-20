package uk.ac.standrews.cs.sos.configurations;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TestConfiguration implements SeaConfiguration {

    private static final String HOME = System.getProperty("user.home") + "/sos/test/";
    private static final String DATA_LOCATION = HOME + "data/";
    private static final String MANIFESTS_LOCATION = HOME + "manifests/";
    private static final String PRIVATE_KEY_FILE = HOME + "keys/private.der";
    private static final String PUBLIC_KEY_FILE = HOME + "keys/public.der";

    @Override
    public String getDataPath() {
        return DATA_LOCATION;
    }

    @Override
    public String getLocalManifestsLocation() {
        return MANIFESTS_LOCATION;
    }

    @Override
    public String[] getIdentityPaths() {
        return new String[]{PRIVATE_KEY_FILE, PUBLIC_KEY_FILE};
    }
}