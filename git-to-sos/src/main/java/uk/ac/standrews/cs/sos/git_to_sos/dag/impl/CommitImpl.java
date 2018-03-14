/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module git-to-sos.
 *
 * git-to-sos is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * git-to-sos is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with git-to-sos. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
