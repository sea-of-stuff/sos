package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.sos.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface LocalNode extends Node {

    Client getClient();

    Storage getStorage();

    DDS getDDS();

    NDS getNDS();

    MCS getMCS();

    Identity getIdentity();

    void kill();
}
