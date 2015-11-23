package interfaces.entities;

import interfaces.components.GUID;
import interfaces.components.Location;

/**
 * A compound is an immutable, optionally labelled, collection of (references to)
 * atoms or other compounds (contents). Compounds do not contain data
 * - they refer to data - and are identified by GUID (derived from their contents).
 *
 * Intuition: <br>
 * Compounds are provided to permit related atoms and compounds to be gathered
 * together (think of folders, zip files, packages etc. without containment).
 *
 * A compound can be used for de-duplication. Two collections of data
 * (atoms and compounds) might contain the same content. The data does not have
 * to be duplicated for each compound, since we can uniquely refer to the data
 * from the compound itself.
 *
 * @see GUID

 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Compound {

    // get a list of locations?
}
