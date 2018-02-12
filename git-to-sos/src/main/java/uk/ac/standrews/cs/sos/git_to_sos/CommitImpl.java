package uk.ac.standrews.cs.sos.git_to_sos;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommitImpl extends EntityImpl implements Commit {

    List<Commit> previous;
    List<Commit> next;

    public CommitImpl(String id) {
        super(id);

        previous = new LinkedList<>();
        next = new LinkedList<>();
    }

    @Override
    public List<Commit> getPrevious() {
        return previous;
    }

    @Override
    public void addPrevious(List<Commit> previous) {

        previous.addAll(previous);
    }

    @Override
    public List<Commit> getNext() {
        return null;
    }

    @Override
    public void addNext() {

    }

    @Override
    public Tree getTree() {
        return null;
    }

}
