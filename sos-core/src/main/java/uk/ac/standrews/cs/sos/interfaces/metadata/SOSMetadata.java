package uk.ac.standrews.cs.sos.interfaces.metadata;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SOSMetadata {

    String getProperty(String propertyName);

    String[] getAllPropertyNames();

    IGUID guid() throws GUIDGenerationException;

    String tabularFormat();

    // String print();
    // List<Tuple<String, String>> getTuples()
}
