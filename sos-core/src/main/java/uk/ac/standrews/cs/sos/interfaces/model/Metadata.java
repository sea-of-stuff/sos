package uk.ac.standrews.cs.sos.interfaces.model;

/**
 *
 * Example:
 *
 * {
 *  "GUID" : "3f845edc76b7e892ddca1f6e290750fe805e7f00",
 *  "Type" : "Metadata",
 *  "Properties" : [
 *      {
 *          "Key" : "Owner",
 *          "Type" : "GUID",
 *          "Value" : "abb134200a"
 *      },
 *      {
 *          "Key" : "Size",
 *          "Type" : "INT",
 *          "Value" : 105
 *      },
 *      {
 *          "Key" : "Timestamp",
 *          "Type" : INT,
 *          "Value" : 1487606187
 *      },
 *      {
 *          "Key" : "Content-Type",
 *          "Type" : STRING,
 *          "Value" : "application/octet-stream"
 *      }
 *   ]
 * }
 *
 *
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
