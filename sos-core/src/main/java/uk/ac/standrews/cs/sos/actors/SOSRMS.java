package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.actors.RMS;
import uk.ac.standrews.cs.sos.interfaces.model.Role;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSRMS implements RMS {

    @Override
    public void add(Role role) {

    }

    @Override
    public Role get(IGUID guid) {
        return null;
    }

    @Override
    public Role active() {
        return null; // TODO - must have default role
    }

    @Override
    public Role setActive(Role role) {
        return null;
    }

    @Override
    public Role setActive(IGUID guid) {
        return null;
    }
}
