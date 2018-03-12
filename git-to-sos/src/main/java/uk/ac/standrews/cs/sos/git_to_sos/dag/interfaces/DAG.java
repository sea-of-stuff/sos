package uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces;

import java.util.HashMap;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface DAG {

    void setRoot(Commit root);

    Commit getRoot();

    HashMap<String, Commit> getCommits();

    HashMap<String, Tree> getTrees();

    HashMap<String, Blob> getBlobs();
}
