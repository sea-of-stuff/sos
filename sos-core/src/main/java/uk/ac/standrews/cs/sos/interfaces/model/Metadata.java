package uk.ac.standrews.cs.sos.interfaces.model;

/**
 *
 * Example:
 *
 * {
 *  "GUID" : "3f845edc76b7e892ddca1f6e290750fe805e7f00",
 *  "Type" : "Metadata",
 *  "Properties" : {
 *          "Author" : "Simone",
 *          "Size" : "105",
 *          "Timestamp" : "1487606187",
 *          "Content-type" : "application/octet-stream"
 *      }
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Metadata extends Manifest {

    Object getProperty(String propertyName);

    String getPropertyAsString(String propertyName);
    Integer getPropertyAsInteger(String propertyName);

    boolean hasProperty(String propertyName);

    String[] getAllPropertyNames();

    String[] getAllFilteredPropertyNames();

    String metadata();
}
