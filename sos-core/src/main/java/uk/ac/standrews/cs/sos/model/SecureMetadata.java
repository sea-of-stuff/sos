package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.sos.impl.json.SecureMetadataDeserializer;
import uk.ac.standrews.cs.sos.impl.json.SecureMetadataSerializer;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonDeserialize(using = SecureMetadataDeserializer.class)
@JsonSerialize(using = SecureMetadataSerializer.class)
public interface SecureMetadata extends Metadata, SecureManifest {}
