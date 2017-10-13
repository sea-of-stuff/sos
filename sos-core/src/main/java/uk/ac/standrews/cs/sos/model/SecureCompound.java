package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.sos.impl.json.SecureCompoundManifestDeserializer;
import uk.ac.standrews.cs.sos.impl.json.SecureCompoundManifestSerializer;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = SecureCompoundManifestSerializer.class)
@JsonDeserialize(using = SecureCompoundManifestDeserializer.class)
public interface SecureCompound extends Compound, SecureManifest {}
