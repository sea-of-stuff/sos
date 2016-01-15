package sos.configurations;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SeaConfiguration {

    String getLocalManifestsLocation();

    String[] getIdentityPaths();

    // XXX - other configs, such as #threads running, etc, could be useful
}
