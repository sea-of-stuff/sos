package uk.ac.standrews.cs.sos.interfaces.context;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextVersionInfo;

import java.util.Map;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ContextsContentsDirectory {

    void addEntry(IGUID context, IGUID version, ContextVersionInfo contextVersionInfo);

    void evict(IGUID context, IGUID version);

    ContextVersionInfo getEntry(IGUID context, IGUID version);

    boolean entryExists(IGUID context, IGUID version);

    Set<IGUID> getVersionsThatPassedPredicateTest(IGUID context);

    Map<IGUID, ContextVersionInfo> getContentsThatPassedPredicateTestRows(IGUID context);

}
