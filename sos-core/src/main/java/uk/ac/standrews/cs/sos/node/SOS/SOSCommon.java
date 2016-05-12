package uk.ac.standrews.cs.sos.node.SOS;

import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.node.Roles;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManager;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SOSCommon implements SeaOfStuff {

    protected Identity identity;
    protected ManifestsManager manifestsManager;
    final protected SeaConfiguration configuration;

    private Roles role;

    public SOSCommon(SeaConfiguration configuration, ManifestsManager manifestsManager, Identity identity, Roles role) {
        this.configuration = configuration;
        this.manifestsManager = manifestsManager;
        this.identity = identity;

        this.role = role;
    }

    @Override
    public Identity getIdentity() {
        return this.identity;
    }

    @Override
    public Roles getRoleMask() {
        return role;
    }
}
