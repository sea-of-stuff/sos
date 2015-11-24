package model.interfaces.components.entities;

import java.util.Collection;

/**
 * A compound is an immutable collection of (references to)
 * atoms or other compounds (contents).
 * FIXME - Compounds do not contain data
 * - they refer to data - and are identified by GUID (derived from their contents).
 *
 * <p>
 * Intuition: <br>
 * Compounds are provided to permit related atoms and compounds to be gathered
 * together (think of folders, zip files, packages etc. without containment).
 * <p>
 * A compound can be used for de-duplication. Two collections of data
 * (atoms and compounds) might contain the same content. The data does not have
 * to be duplicated for each compound, since we can uniquely refer to the data
 * from the compound itself.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Compound extends Union {

    /**
     * Get the contents of this compound. The contents of a compound are either Atoms
     * or other Compounds.
     *
     * @return the contents of this compound.
     */
    Collection<Union> getContents();
}
