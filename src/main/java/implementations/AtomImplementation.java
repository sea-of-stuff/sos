package implementations;

import interfaces.components.Location;
import interfaces.entities.Atom;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO - have two classes: AtomInMemory and AtomImplementation??
 * TODO - look at GUDE
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomImplementation extends InputStream implements Atom {

    public AtomImplementation(byte[] bytes) {
    }

    public AtomImplementation(Location location) {
    }

    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public int read() throws IOException {
        // TODO
        return 0;
    }
}
