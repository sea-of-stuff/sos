package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.model.*;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class ContextImpl implements Context {

    private IGUID guid;
    private String name;
    protected SOSPredicate predicate;
    protected Node[] sources;

    private static int EMPTY_ARRAY = 0;

    @Override
    public ManifestType getType() {
        return ManifestType.CONTEXT;
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public Context setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Context setSources(Node[] nodes) {
        sources = nodes;
        return this;
    }

    @Override
    public Context build() {
        guid = GUIDFactory.generateRandomGUID();
        return this;
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
    public boolean verifySignature(Role role) throws ManifestVerificationException {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String toString() {
        return "Context GUID: " + guid + ", Name: " + name;
    }
}
