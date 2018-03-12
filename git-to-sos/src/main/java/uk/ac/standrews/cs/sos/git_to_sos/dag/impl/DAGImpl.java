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
