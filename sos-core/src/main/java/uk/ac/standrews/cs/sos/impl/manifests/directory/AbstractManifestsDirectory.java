package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AbstractManifestsDirectory implements ManifestsDirectory {

    abstract void advanceTip(IGUID invariant, IGUID version);
    abstract void advanceTip(IGUID invariant, Set<IGUID> previousVersions, IGUID newVersion);

    @Override
    public void advanceTip(Version version) {

        Set<IGUID> previousVersions = version.getPreviousVersions();

        if (previousVersions == null || previousVersions.isEmpty()) {
            advanceTip(version.getInvariantGUID(), version.guid());
        } else {
            advanceTip(version.getInvariantGUID(), version.getPreviousVersions(), version.guid());
        }

    }
}
