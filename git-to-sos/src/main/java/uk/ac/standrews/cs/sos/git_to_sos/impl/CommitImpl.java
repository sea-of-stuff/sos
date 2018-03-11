package uk.ac.standrews.cs.sos.git_to_sos.impl;

import uk.ac.standrews.cs.sos.git_to_sos.interfaces.Commit;
import uk.ac.standrews.cs.sos.git_to_sos.interfaces.Tree;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommitImpl extends EntityImpl implements Commit {

    private List<Commit> previous;
    private List<Commit> next;

    private Tree tree;

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

        this.previous.addAll(previous);
    }

    @Override
    public List<Commit> getNext() {
        return null;
    }

    @Override
    public void addNext() {

    }

    @Override
    public void setTree(Tree tree) {

        this.tree = tree;
    }

    @Override
    public Tree getTree() {
        return tree;
    }

}
