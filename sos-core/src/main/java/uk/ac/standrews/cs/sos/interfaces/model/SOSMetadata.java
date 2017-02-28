package uk.ac.standrews.cs.sos.interfaces.model;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;

/**
 * Example:
 *
 * {
 *  "GUID" : "3f845edc76b7e892ddca1f6e290750fe805e7f00",
 *  "Properties" : [
 *      {
 *          "Key" : "Author",
 *          "Value" : "simone"
 *      }, {
 *          "Key" : "Size",
 *          "Value" : "105"
 *      }, {
 *          "Key" : "Timestamp",
 *          "Value" : "1487606187"
 *      }, {
 *          "Key" : "Content-Type",
 *          "Value" : "application/octet-stream"
 *      }
 *  ]
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SOSMetadata {

    String getProperty(String propertyName);

    boolean hasProperty(String propertyName);

    String[] getAllPropertyNames();

    String[] getAllFilteredPropertyNames();

    IGUID guid() throws GUIDGenerationException;

    String metadata();
}
