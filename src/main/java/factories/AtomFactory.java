package factories;

import implementations.AtomImplementation;
import interfaces.components.Location;
import interfaces.entities.Atom;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomFactory {

    /**
     * Makes an atom given a sequence of bytes from memory. The atom is persisted
     * in the Sea of Stuff at a given {@link Location}
     * @param bytes sequence of bytes of this atom
     * @return Atom
     */
    public Atom makeAtom(byte[] bytes) {
        return new AtomImplementation(bytes);
    }

    /**
     * Makes an atom given a location in the Sea of Stuff. A sequence of bytes
     * is retrieved from the given location and it is used to make the atom.
     * @param location  where the bytes used to make the atom are
     * @return Atom
     */
    public Atom makeAtom(Location location) {
        return new AtomImplementation(location);
    }
}
