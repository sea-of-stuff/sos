package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.List;

/**
 * This class contains useful Predicates that can be used by the contexts, as defined by the Context interface
 * @see uk.ac.standrews.cs.sos.model.Context
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonPredicates {

    private CommonPredicates() {}

    /**
     * This method constructs a predicate that checks that the metadata of some given version matches any of the content-types specified
     *
     * FIXME - Usage example:
     *
     * return new SOSPredicateImpl(
     *      CommonPredicates.ContentTypePredicate(Collections.singletonList("application/octet-stream")),
     *      PREDICATE_ALWAYS_TRUE);
     *
     * @param matchingContentTypes the content types that should be matched
     * @return the predicate
     */
    public static boolean ContentTypePredicate(IGUID guid, List<String> matchingContentTypes) {

        SOSAgent agent = SOSAgent.instance();

        try {
            String contentType = (String) agent.getMetaProperty(guid, MetadataConstants.CONTENT_TYPE);
            return matchingContentTypes.contains(contentType);

        } catch (Exception e) {
            // This could occur because the metadata could not be found or the type property was not available
            SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
        }

        return false;

    }

}