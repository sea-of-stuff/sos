package uk.ac.standrews.cs.sos.git_to_sos.dag.impl;

import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.Commit;
import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.Tree;

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
    public void addPrevious(Commit previous) {

        this.previous.add(previous);
    }

    @Override
    public List<Commit> getPrevious() {
        return previous;
    }

    @Override
    public void addNext(Commit next) {

        this.next.add(next);
    }

    @Override
    public List<Commit> getNext() {
        return next;
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
