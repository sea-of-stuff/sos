package uk.ac.standrews.cs.sos.impl.datamodel.builders;

import uk.ac.standrews.cs.sos.model.Role;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class ManifestBuilder {

    protected Role role = null;
    private boolean protect = false;

    public ManifestBuilder setRole(Role role) {
        this.role = role;

        return this;
    }

    public Role getRole() {
        return role;
    }

    public ManifestBuilder setProtectFlag(boolean protect) {
        this.protect = protect;

        return this;
    }

    public boolean isProtect() {
        return protect;
    }

}
