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
package uk.ac.standrews.cs.sos.git_to_sos;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.git_to_sos.dag.impl.BlobImpl;
import uk.ac.standrews.cs.sos.git_to_sos.dag.impl.CommitImpl;
import uk.ac.standrews.cs.sos.git_to_sos.dag.impl.DAGImpl;
import uk.ac.standrews.cs.sos.git_to_sos.dag.impl.TreeImpl;
import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.*;
import uk.ac.standrews.cs.sos.git_to_sos.transformation_strategies.OneToOne;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.eclipse.jgit.lib.Constants.OBJ_BLOB;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GitToSOS {

    public static void main(String[] args) throws IOException, GitAPIException, SOSException, ConfigurationException {

        String[] repos = new String[]{
                "git-to-sos/src/main/resources/test-git",
                "git-to-sos/src/main/resources/one-file-commit",
                "git-to-sos/src/main/resources/one-large-file-commit",
                "git-to-sos/src/main/resources/one-larger-file-commit",
                "git-to-sos/src/main/resources/three-commits-text-file"
        };

        GitToSOS gitToSOS = new GitToSOS(repos[4], false);

        System.out.println("\n-------------------------------------");
        System.out.println("Starting SOS Node");
        File configFile = new File("example_config.json");
        SettingsConfiguration configuration = new SettingsConfiguration(configFile);
        SOSLocalNode sos = startSOS(configuration.getSettingsObj());
        System.out.println("SOS Node running");
        System.out.println("-------------------------------------\n");

        // Perform transformations
        new OneToOne(sos, gitToSOS.getDag()).transform();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sos.kill(true);
        }
    }

    /**********************************/
    /********* GitToSOS class *********/
    /**********************************/

    private DAG dag;
    private boolean printBlobData;

    private GitToSOS(String path, boolean printBlobData) throws IOException, GitAPIException {

        this.printBlobData = printBlobData;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try (Repository repository = builder.setGitDir(new File(path + "/.git"))
                .readEnvironment()  // scan environment GIT_* variables
                .findGitDir()       // scan up the file system tree
                .build()) {

            System.out.println("Repository: " + repository.getDirectory());
            dag = new DAGImpl();
            try (Git git = new Git(repository)) {

                List<Ref> call = git.branchList().call();
                for (Ref ref : call) {
                    walkBranch(repository, ref);
                }
            }
        }

    }

    public DAG getDag() {
        return dag;
    }

    private void walkBranch(Repository repository, Ref branchRef) throws IOException {

        System.out.println("-------------------------------------");
        System.out.println("-------------------------------------");
        System.out.println("Branch: " + branchRef + " Name: " + branchRef.getName() + " SHA1: " + branchRef.getObjectId().getName());

        HashMap<String, Commit> commits = dag.getCommits();

        // A RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(branchRef.getObjectId());
            System.out.println("Start-Commit: " + commit);
            System.out.println("-------------------------------------");

            int count = 0;
            RevCommit currentRev = null;
            walk.markStart(commit);
            for (RevCommit rev : walk) {
                currentRev = rev;
                Commit currentCommit;
                String currentCommitId = currentRev.getId().name();
                if (commits.containsKey(currentCommitId)) {
                    currentCommit = commits.get(currentCommitId);
                } else {
                    currentCommit = new CommitImpl(currentCommitId);
                }

                Tree tree = processCommit(repository, rev);
                currentCommit.setTree(tree);

                commits.put(currentCommitId, currentCommit);
                processPreviousCommits(currentRev, currentCommitId);
                processNextCommits(currentCommit);

                count++;
            }

            // Last visited commit of the master branch is the root of the DAG
            if (currentRev != null && branchRef.getName().equals("refs/heads/master")) {
                Commit rootCommit = commits.get(currentRev.getId().name());
                dag.setRoot(rootCommit);
            }

            System.out.println("-------------------------------------");
            System.out.println("Number of commits in branch: " + count);

            walk.dispose();
        }
    }

    private void processPreviousCommits(RevCommit rev, String currentCommitId) {

        HashMap<String, Commit> commits = dag.getCommits();
        for(RevCommit prev:rev.getParents()) {
            String id = prev.getId().name();
            Commit prevCommit;
            if (commits.containsKey(id)) {
                prevCommit = commits.get(id);
            } else {
                prevCommit = new CommitImpl(id);
                commits.put(id, prevCommit);
            }

            commits.get(currentCommitId).addPrevious(prevCommit);
        }
    }

    private void processNextCommits(Commit current) {

        HashMap<String, Commit> commits = dag.getCommits();

        // Make current the next of previous
        for(Commit prev:current.getPrevious()) {
            commits.get(prev.getId()).addNext(current);
        }
    }

    private Tree processCommit(Repository repository, RevCommit commit) throws IOException {
        System.out.println("Commit: " + commit);
        System.out.println("\tMessage: " + commit.getShortMessage());

        HashMap<String, Tree> trees = dag.getTrees();

        RevTree revTree = commit.getTree();
        System.out.println("Tree: " + revTree);
        String currentTreeId = revTree.getId().getName();
        Tree tree = new TreeImpl(currentTreeId);
        trees.put(currentTreeId, tree);

        // now use a TreeWalk to iterate over all files in the Tree recursively
        // you can set Filters to narrow down the results if needed
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(revTree);
            treeWalk.setRecursive(false);

            int idx = 0;
            while (treeWalk.next()) {
                ObjectId objId = treeWalk.getObjectId(0);
                String name = treeWalk.getPathString();
                String objSHA1 = objId.getName();

                System.out.println("[ " + idx + " ] Entity path: " + name + " - SHA1: " + objSHA1);

                ObjectLoader objectLoader = repository.getObjectDatabase().open(objId);
                if (objectLoader.getType() == OBJ_BLOB) {
                    Entity blob = readBlob(repository, treeWalk.getObjectId(0));
                    trees.get(currentTreeId).addContent(name, blob);
                } else {
                    if (treeWalk.isSubtree()) {
                        if (!trees.containsKey(objSHA1)) {
                            currentTreeId = objSHA1;

                            Tree subtree = new TreeImpl(currentTreeId);
                            if (trees.containsKey(currentTreeId)) {
                                trees.get(currentTreeId).addContent(name, subtree);
                            }
                            trees.put(currentTreeId, subtree);
                        }

                        System.out.println(">> Subtree");
                        treeWalk.enterSubtree();
                    }
                }

                idx++;
            }

        }

        System.out.println(); // print empty line

        return tree;
    }

    private Entity readBlob(Repository repository, AnyObjectId id) throws IOException {

        HashMap<String, Blob> blobs = dag.getBlobs();

        ObjectLoader loader = repository.open(id);
        System.out.print("Blob: ");
        if (printBlobData) {
            loader.copyTo(System.out);
        } else {
            System.out.println("N/A");
        }

        String blobSHA = id.getName();
        if (!blobs.containsKey(blobSHA)) {
            Blob blob = new BlobImpl(blobSHA, loader.openStream());
            blobs.put(blobSHA, blob);
        }

        return blobs.get(blobSHA);
    }

    private static SOSLocalNode startSOS(SettingsConfiguration.Settings settings) throws SOSException {

        LocalStorage localStorage;
        try {
            CastoreBuilder builder = settings.getStore().getCastoreBuilder();
            IStorage storage = CastoreFactory.createStorage(builder);
            localStorage = new LocalStorage(storage);
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();

        return builder.settings(settings)
                .internalStorage(localStorage)
                .build();
    }
}
