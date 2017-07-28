package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.impl.services.SOSAgent;
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
     * Accept everything.
     *
     * @return true
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean AcceptAll() {

        return true;
    }

    /**
     * This method constructs a predicate that checks that the metadata of some given version matches any of the properties specified
     *
     * @param matchingContentTypes the content types that should be matched
     * @return the predicate
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean MetadataPropertyPredicate(IGUID guid, String property, List<String> matchingContentTypes) {

        SOSAgent agent = SOSAgent.instance();

        try {
            String contentType = (String) agent.getMetaProperty(guid, property);
            return matchingContentTypes.contains(contentType);

        } catch (Exception e) {
            // This could occur because the metadata could not be found or the type property was not available
            SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
        }

        return false;
    }

    public static boolean ContentTypePredicate(IGUID guid, List<String> matchingContentTypes) {

       return MetadataPropertyPredicate(guid, MetadataConstants.CONTENT_TYPE, matchingContentTypes);
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean MetadataIntPropertyPredicate(IGUID guid, String property, Integer matchingValue) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Integer value = (Integer) agent.getMetaProperty(guid, property);
            return value.equals(matchingValue);

        } catch (Exception e) {
            // This could occur because the metadata could not be found or the type property was not available
            SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
        }

        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean MetadataIntGreaterPropertyPredicate(IGUID guid, String property, Integer matchingValue) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Integer value = (Integer) agent.getMetaProperty(guid, property);
            return value.compareTo(matchingValue) > 0;

        } catch (Exception e) {
            // This could occur because the metadata could not be found or the type property was not available
            SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
        }

        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean MetadataIntLessPropertyPredicate(IGUID guid, String property, Integer matchingValue) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Integer value = (Integer) agent.getMetaProperty(guid, property);
            return value.compareTo(matchingValue) < 0;

        } catch (Exception e) {
            // This could occur because the metadata could not be found or the type property was not available
            SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
        }

        return false;
    }

    /**
     * Check if the manifest with the given GUID is signed by a Role matching the signer GUID
     * @param guid
     * @param signer
     * @return
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean SignedBy(IGUID guid, IGUID signer) {

        SOSAgent agent = SOSAgent.instance();

        try {
            IGUID signerFound = GUIDFactory.recreateGUID((String) agent.getMetaProperty(guid, "signer")); // FIXME - use appropriate method from Metadata obj
            return signerFound.equals(signer);

        } catch (ManifestNotFoundException | MetadataNotFoundException | GUIDGenerationException e) {

            return false;
        }

    }


    public static boolean CheckDataOnTheFly() {

        // TODO - make some code that checks the data
        return false;
    }

}


/**

 ## Ideas for advanced predicates

 - can get geo-location using the service ipinfo.io
 example:
 curl ipinfo.io
 curl ipinfo.io/138.250.10.10

 more here: https://ipinfo.io/developers
 */