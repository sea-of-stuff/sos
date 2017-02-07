package uk.ac.standrews.cs.sos.filesystem;

import uk.ac.standrews.cs.IGUID;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CurrentAsset {

    private IGUID invariant;
    private IGUID version;

    public CurrentAsset(IGUID invariant, IGUID version) {
        this.invariant = invariant;
        this.version = version;
    }

    public IGUID getVersion() {
        return version;
    }

    public IGUID getInvariant() {
        return invariant;
    }
}
