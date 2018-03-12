package uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces;

import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Commit extends Entity {

    void addPrevious(Commit previous);
    List<Commit> getPrevious();

    void addNext(Commit commit);
    List<Commit> getNext();

    void setTree(Tree tree);
    Tree getTree();
}
