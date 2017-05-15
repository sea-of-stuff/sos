package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.model.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonContext implements Context {

    protected IGUID guid;
    protected String name;
    protected SOSPredicate predicate;
    protected Node[] sources;

    private static int EMPTY_ARRAY = 0;

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract SOSPredicate predicate();

    @Override
    public Policy[] policies() {
        return new Policy[EMPTY_ARRAY];
    }

    @Override
    public Node[] whereToRun() {
        return new Node[EMPTY_ARRAY];
    }

    @Override
    public String toString() {
        return "Context GUID: " + guid + ", Name: " + name;
    }

    /**
     * Utility function
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
