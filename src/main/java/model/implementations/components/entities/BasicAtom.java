package model.implementations.components.entities;

import IO.sources.DataSource;
import model.interfaces.components.entities.Atom;

/**
 * Basic implementation of the Atom entity.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicAtom implements Atom {

    private DataSource source;

    /**
     * Creates an atom given an input source.
     *
     * @param source used for this atom.
     */
    public BasicAtom(DataSource source) {
        this.source = source;
    }

    @Override
    public DataSource getSource() {
        return this.source;
    }
}
