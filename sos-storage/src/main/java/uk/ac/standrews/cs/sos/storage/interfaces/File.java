package uk.ac.standrews.cs.sos.storage.interfaces;

import uk.ac.standrews.cs.sos.storage.data.Data;
import uk.ac.standrews.cs.sos.storage.exceptions.DataException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface File extends StatefulObject {

    Data getData() throws DataException;
}
