package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BaseContext implements Context {

    protected IGUID guid;
    protected String name;
    protected SOSPredicate predicate;
    protected NodesCollection domain;
    protected NodesCollection codomain;

    private static int EMPTY_ARRAY = 0;

    public BaseContext(String name, NodesCollection domain, NodesCollection codomain) {
        this(GUIDFactory.generateRandomGUID(), name, domain, codomain);
    }

    public BaseContext(IGUID guid, String name, NodesCollection domain, NodesCollection codomain) {
        this.guid = guid;
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SOSPredicate predicate() {

        return new SOSPredicateImpl(guid -> false, Long.MAX_VALUE);
    }

    @Override
    public Policy[] policies() {
        return new Policy[EMPTY_ARRAY];
    }

    @Override
    public NodesCollection whereToRun() {
        return null;
    }

    @Override
    public String toString() {
        return "Context GUID: " + guid + ", Name: " + name;
    }


    protected Predicate<IGUID> contentTypePredicate(List<String> matchingFormats) {

        return guid -> {
            SOSAgent agent = SOSAgent.instance();

            try {
                String contentType = getMetaProperty(agent, guid, MetadataConstants.CONTENT_TYPE);
                return matchingFormats.contains(contentType);

            } catch (Exception e) {
                // This could occur because the metadata could not be found or the type property was not available
                SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
            }

            return false;
        };
    }

    /**
     * Utility function
     *
     * todo: make agent method to get property
     *
     * @param agent
     * @param guid
     * @param property
     * @return
     * @throws ManifestNotFoundException
     * @throws MetadataNotFoundException
     */
    protected String getMetaProperty(SOSAgent agent, IGUID guid, String property) throws ManifestNotFoundException, MetadataNotFoundException {
        Version version = (Version) agent.getManifest(guid);

        Metadata metadata = agent.getMetadata(version.getMetadata());
        return metadata.getPropertyAsString(property);
    }
}
