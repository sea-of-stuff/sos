package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.sos.interfaces.actors.*;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface LocalNode extends Node {

    Agent getAgent();

    Storage getStorage();

    DDS getDDS();

    NDS getNDS();

    MMS getMMS();

    CMS getCMS();

    RMS getRMS();

    Identity getIdentity(); // TODO - remove

    void kill();
}
