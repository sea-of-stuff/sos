package interfaces.entities;

import interfaces.components.GUID;
import interfaces.components.Location;

/**
 * An atom is an immutable sequence of bytes uniquely identified by a
 * GUID. The GUID is deterministically derived from the atom's sequence
 * of bytes. Atoms are uniquely identifiable within the Sea of Stuff.
 *
 * <p>
 * Intuition: <br>
 * A GUID is a shorthand for a sequence of bytes. GUIDs and the sequences of
 * bytes that they represent are in 1:1 correspondence and once created are good
 * for all time.
 *
 * @see GUID
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Atom {

    /**
     * TODO - wrap using InputStream
     * @return
     */
    byte[] getBytes();
}
