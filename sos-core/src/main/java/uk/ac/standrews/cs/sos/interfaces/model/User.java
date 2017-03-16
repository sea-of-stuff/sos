package uk.ac.standrews.cs.sos.interfaces.model;

import uk.ac.standrews.cs.IGUID;

import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface User {

    IGUID guid();

    String getName();  // e.g.  Simone Ivan Conte

    PublicKey getPubkey();

}
