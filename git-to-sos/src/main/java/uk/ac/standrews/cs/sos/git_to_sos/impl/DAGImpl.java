package uk.ac.standrews.cs.sos.git_to_sos.impl;

import uk.ac.standrews.cs.sos.git_to_sos.interfaces.Commit;
import uk.ac.standrews.cs.sos.git_to_sos.interfaces.DAG;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DAGImpl implements DAG {

    private Commit root;

    public DAGImpl(Commit root) {
        this.root = root;
    }

    @Override
    public Commit getRoot() {
        return root;
    }
}
