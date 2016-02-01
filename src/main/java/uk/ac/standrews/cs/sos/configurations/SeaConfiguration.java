package uk.ac.standrews.cs.sos.configurations;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SeaConfiguration {

    // TODO - javadocs
    // TODO - load from file?

    String getDataPath();

    String getLocalManifestsLocation();

    String[] getIdentityPaths();

    String getIndexPath();

    String getCacheDataPath();

    // XXX - other configs, such as #threads running, etc, could be useful
}
