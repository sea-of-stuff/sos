package uk.ac.standrews.cs.sos.impl;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.Scope;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ScopeImpl implements Scope {

    private Node[] nodes;
    private TYPE type;
    private IGUID guid;

    public ScopeImpl(Node[] nodes) {
        this(TYPE.RESTRICTED);
        this.nodes = nodes;
    }

    /**
     * If type is ANY
     *
     * if type is RESTRICTED
     *
     *
     * @param type
     */
    public ScopeImpl(TYPE type) {
        this.type = type;

        guid = GUIDFactory.generateRandomGUID();
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public Node[] nodes() {
        return nodes;
    }

    @Override
    public TYPE type() {
        return type;
    }
}
