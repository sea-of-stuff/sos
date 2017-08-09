package uk.ac.standrews.cs.sos.impl.manifests.builders;

import uk.ac.standrews.cs.sos.model.Role;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class ManifestBuilder {

    private Role role = null;

    public ManifestBuilder setRole(Role role) {
        this.role = role;

        return this;
    }

    public Role getRole() {
        return role;
    }
}
