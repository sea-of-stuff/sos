package uk.ac.standrews.cs.sos.interfaces.metadata;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SOSMetadata {

    String getProperty(String propertyName);

    String[] getAllPropertyNames();

    // String print();
    // List<Tuple<String, String>> getTuples()
}
