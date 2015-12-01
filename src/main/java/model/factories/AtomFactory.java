package model.factories;

import IO.sources.DataSource;
import model.implementations.components.entities.BasicAtom;
import model.implementations.utils.Location;
import model.interfaces.components.entities.Atom;

/**
 * The AtomFactory abstracts the complexity in building an Atom given a DataSource.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomFactory {

    // Suppresses default constructor, ensuring non-instantiability.
    private AtomFactory() {}

    /**
     * Makes an atom given a data source.
     * We assume that the returned atom is persisted in the Sea of Stuff at a given location.
     *
     * @param source source of bytes used to create an Atom
     * @return Atom
     *
     * @see Location
     */
    public static Atom makeAtom(DataSource source) {
        return new BasicAtom(source);
    }
}
