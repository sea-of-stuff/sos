package uk.ac.standrews.cs.sos.impl.datamodel.builders;

import uk.ac.standrews.cs.sos.model.Role;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class ManifestBuilder {

    protected Role role = null;

    public abstract ManifestBuilder setRole(Role role);

    public Role getRole() {
        return role;
    }
}
