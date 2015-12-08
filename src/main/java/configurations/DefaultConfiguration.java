package configurations;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DefaultConfiguration implements SeaConfiguration {

    private static final String MANIFESTS_LOCATION = "/tmp/sos/manifests/";

    @Override
    public String getLocalManifestsLocation() {
        return MANIFESTS_LOCATION;
    }
}
