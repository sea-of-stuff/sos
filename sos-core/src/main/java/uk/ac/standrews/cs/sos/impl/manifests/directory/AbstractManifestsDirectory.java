package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AbstractManifestsDirectory implements ManifestsDirectory {

    abstract void advanceHead(IGUID invariant, IGUID version);
    abstract void advanceHead(IGUID invariant, Set<IGUID> previousVersions, IGUID newVersion);

    @Override
    public void advanceHead(Version version) {

        Set<IGUID> previousVersions = version.getPreviousVersions();

        if (previousVersions == null || previousVersions.isEmpty()) {
            advanceHead(version.getInvariantGUID(), version.guid());
        } else {
            advanceHead(version.getInvariantGUID(), version.getPreviousVersions(), version.guid());
        }

    }
}
