package uk.ac.standrews.cs.sos.git_to_sos.interfaces;

import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Commit extends Entity {

    List<Commit> getPrevious();
    void addPrevious(List<Commit> previous);

    List<Commit> getNext();
    void addNext();

    Tree getTree();
}
