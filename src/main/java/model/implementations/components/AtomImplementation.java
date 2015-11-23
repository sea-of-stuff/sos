package model.implementations.components;

import IO.sources.DataSource;
import model.interfaces.entities.Atom;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO - have two classes: AtomInMemory and AtomImplementation??
 * TODO - look at GUDE
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomImplementation extends InputStream implements Atom {

    private DataSource source;

    /**
     * Created an atom given an input source.
     *
     * @param source
     */
    public AtomImplementation(DataSource source) {
        this.source = source;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public DataSource getSource() {
        return this.source;
    }
}
