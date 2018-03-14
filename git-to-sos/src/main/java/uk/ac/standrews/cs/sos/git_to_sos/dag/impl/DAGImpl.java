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

import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.Blob;
import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.Commit;
import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.DAG;
import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.Tree;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DAGImpl implements DAG {

    private Commit root;

    private HashMap<String, Commit> commits = new LinkedHashMap<>();
    private HashMap<String, Tree> trees = new LinkedHashMap<>();
    private HashMap<String, Blob> blobs = new LinkedHashMap<>();

    @Override
    public void setRoot(Commit root) {
        this.root = root;
    }

    @Override
    public Commit getRoot() {
        return root;
    }

    @Override
    public HashMap<String, Commit> getCommits() {
        return commits;
    }

    @Override
    public HashMap<String, Tree> getTrees() {
        return trees;
    }

    @Override
    public HashMap<String, Blob> getBlobs() {
        return blobs;
    }
}
