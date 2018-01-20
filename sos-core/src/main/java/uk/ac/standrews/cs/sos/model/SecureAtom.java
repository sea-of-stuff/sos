package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.sos.impl.json.SecureAtomManifestDeserializer;
import uk.ac.standrews.cs.sos.impl.json.SecureAtomManifestSerializer;

/**
 * This is the interface for the Atom manifest.
 * An atom is the basic building block for the SOS and it is used to abstract data over locations.
 *
 * Example:
 *
 * {
 *  "Type" : "Atom",
 *  "guid" : "da39a3ee5e6b4b0d3255bfef95601890afd80709",
 *  "locations" : [
 *      {
 *      "type" : "cache",
 *      "location" : "sos://3c9bfd93ab9a6e2ed501fc583685088cca66bac2/da39a3ee5e6b4b0d3255bfef95601890afd80709"
 *      }
 *  ]
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = SecureAtomManifestSerializer.class)
@JsonDeserialize(using = SecureAtomManifestDeserializer.class)
public interface SecureAtom extends Atom, SecureManifest {}
