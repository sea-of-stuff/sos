package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;

import java.security.PublicKey;

/**
 * Used to create roles
 * A user has one or more roles
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface User {

    IGUID guid();

    String getName();  // e.g.  Simone Ivan Conte

    PublicKey getPubkey();

}
