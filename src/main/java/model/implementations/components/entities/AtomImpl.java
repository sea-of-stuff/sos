package model.implementations.components.entities;

import IO.sources.DataSource;
import model.interfaces.components.entities.Atom;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomImpl implements Atom {

    private DataSource source;

    /**
     * Created an atom given an input source.
     *
     * @param source
     */
    public AtomImpl(DataSource source) {
        this.source = source;
    }

    @Override
    public DataSource getSource() {
        return this.source;
    }
}
