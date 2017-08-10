package uk.ac.standrews.cs.sos.model;

/**
 * This is the interface for the Atom manifest.
 * An atom is the basic building block for the SOS and it is used to abstract data over locations.
 *
 * Example:
 *
 * {
 *  "Type" : "Atom",
 *  "GUID" : "da39a3ee5e6b4b0d3255bfef95601890afd80709",
 *  "Locations" : [
 *      {
 *      "type" : "cache",
 *      "location" : "sos://3c9bfd93ab9a6e2ed501fc583685088cca66bac2/da39a3ee5e6b4b0d3255bfef95601890afd80709"
 *      }
 *  ]
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SecureAtom extends Atom, SecureManifest {}
