package model.interfaces.components.manifests;

import model.interfaces.components.entities.Atom;

/**
 * Manifest describing an Atom.
 *
 * <p>
 * Manifest - GUID <br>
 * ManifestType - ATOM <br>
 * Timestamp - ? <br>
 * Signature - signature of the manifest <br>
 * Locations - list of locations <br>
 * Content - GUID Content
 * </p>
 *
 * @see Atom
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface AtomManifest extends UnionManifest {

}
