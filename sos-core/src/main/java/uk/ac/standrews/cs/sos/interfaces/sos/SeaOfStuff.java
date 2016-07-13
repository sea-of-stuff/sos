package uk.ac.standrews.cs.sos.interfaces.sos;

import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;

/**
 * The SeaOfStuff interface provides a very simplistic and general abstraction
 * on the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SeaOfStuff {

    /**
     * @return the PolicyManager used by this instance of the SOS
     */
    PolicyManager getPolicyManager();

}
