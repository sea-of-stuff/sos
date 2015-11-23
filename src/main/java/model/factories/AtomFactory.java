package model.factories;

import IO.sources.DataSource;
import model.implementations.components.AtomImplementation;
import model.interfaces.components.utils.Location;
import model.interfaces.entities.Atom;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomFactory {

    /**
     * Makes an atom given a source - either memory or an arbitrary location.
     * The atom is persisted in the Sea of Stuff at a given location TODO - not in all cases
     *
     * @param source source of bytes used to create an Atom
     * @return Atom
     *
     * @see Location
     */
    public Atom makeAtom(DataSource source) {
        return new AtomImplementation(source);
    }
}
