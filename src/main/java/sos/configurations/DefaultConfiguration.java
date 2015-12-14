package sos.configurations;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DefaultConfiguration implements SeaConfiguration {

    private static final String MANIFESTS_LOCATION = "/tmp/sos/manifests/";

    // Suppresses default constructor, ensuring non-instantiability.
    public DefaultConfiguration() {}

    @Override
    public String getLocalManifestsLocation() {
        return MANIFESTS_LOCATION;
    }
}
