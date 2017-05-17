package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.model.*;

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
