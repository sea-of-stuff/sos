package uk.ac.standrews.cs.sos.exceptions.manifest;

import uk.ac.standrews.cs.guid.IGUID;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomNotFoundException extends Exception {

    public AtomNotFoundException(IGUID guid) {
        super("Atom with GUID " + guid.toMultiHash());
    }
}
