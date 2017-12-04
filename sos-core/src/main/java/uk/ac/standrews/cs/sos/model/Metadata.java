package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.sos.impl.json.MetadataDeserializer;
import uk.ac.standrews.cs.sos.impl.json.MetadataSerializer;
import uk.ac.standrews.cs.sos.impl.metadata.MetaProperty;

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
 *          "Type" : "LONG",
 *          "Value" : 105
 *      },
 *      {
 *          "Key" : "Timestamp",
 *          "Type" : "LONG",
 *          "Value" : 1487606187
 *      },
 *      {
 *          "Key" : "Content-Type",
 *          "Type" : "STRING",
 *          "Value" : "application/octet-stream"
 *      }
 *   ]
 * }
 *
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonDeserialize(using = MetadataDeserializer.class)
@JsonSerialize(using = MetadataSerializer.class)
public interface Metadata extends Manifest {

    void addProperty(MetaProperty property);

    MetaProperty getProperty(String propertyName);

    String getPropertyAsString(String propertyName);
    Long getPropertyAsLong(String propertyName);

    boolean hasProperty(String propertyName);

    String[] getAllPropertyNames();

    void generateAndSetGUID();
}
