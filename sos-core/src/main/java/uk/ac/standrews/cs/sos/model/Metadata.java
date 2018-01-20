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
 *  "guid" : "3f845edc76b7e892ddca1f6e290750fe805e7f00",
 *  "Type" : "metadata",
 *  "Properties" : [
 *      {
 *          "key" : "Owner",
 *          "Type" : "guid",
 *          "Value" : "abb134200a"
 *      },
 *      {
 *          "key" : "Size",
 *          "Type" : "LONG",
 *          "Value" : 105
 *      },
 *      {
 *          "key" : "Timestamp",
 *          "Type" : "LONG",
 *          "Value" : 1487606187
 *      },
 *      {
 *          "key" : "Content-Type",
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
public interface Metadata extends SignedManifest {

    MetaProperty getProperty(String propertyName);

    boolean hasProperty(String propertyName);

    String[] getAllPropertyNames();

}
