package uk.ac.standrews.cs.sos.git_to_sos;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import uk.ac.standrews.cs.sos.git_to_sos.impl.BlobImpl;
import uk.ac.standrews.cs.sos.git_to_sos.impl.CommitImpl;
import uk.ac.standrews.cs.sos.git_to_sos.impl.DAGImpl;
import uk.ac.standrews.cs.sos.git_to_sos.impl.TreeImpl;
import uk.ac.standrews.cs.sos.git_to_sos.interfaces.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static org.eclipse.jgit.lib.Constants.OBJ_BLOB;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class GitToSOS {

    private DAG dag;
    private HashMap<String, Commit> commits = new LinkedHashMap<>();
    private HashMap<String, Tree> trees = new LinkedHashMap<>();
    private HashMap<String, Blob> blobs = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException, GitAPIException {

        String[] repos = new String[]{
                "git-to-sos/src/main/resources/test-git",
                "git-to-sos/src/main/resources/one-file-commit"
        };

        GitToSOS gitToSOS = new GitToSOS(repos[0]);
    }

    private GitToSOS(String path) throws IOException, GitAPIException {

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try (Repository repository = builder.setGitDir(new File(path + "/.git"))
                .readEnvironment()  // scan environment GIT_* variables
                .findGitDir()       // scan up the file system tree
                .build()) {

            System.out.println("Repository: " + repository.getDirectory());
            try (Git git = new Git(repository)) {

                List<Ref> call = git.branchList().call();
                for (Ref ref : call) {
                    walkBranch(repository, ref);
                }
            }
        }

    }

    public DAG getDAG() {
        return dag;
    }

    private void walkBranch(Repository repository, Ref branchRef) throws IOException {

        System.out.println("-------------------------------------");
        System.out.println("-------------------------------------");
        System.out.println("Branch: " + branchRef + " " + branchRef.getName() + " SHA1: " + branchRef.getObjectId().getName());

        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(branchRef.getObjectId());
            System.out.println("Start-Commit: " + commit);
            System.out.println("-------------------------------------");

            int count = 0;
            RevCommit currentRev = null;
            walk.markStart(commit);
            for (RevCommit rev : walk) {
                currentRev = rev;
                Commit currentCommit = new CommitImpl(currentRev.getId().name());

                Tree tree = processCommit(repository, rev);
                currentCommit.setTree(tree);

                String currentCommitId = currentCommit.getId();

                commits.put(currentCommitId, currentCommit);
                processPreviousCommits(currentRev, currentCommitId);

                count++;
            }

            // Last visited commit of the master branch is the root of the DAG
            if (currentRev != null && branchRef.getName().equals("refs/heads/master")) {
                Commit rootCommit = commits.get(currentRev.getId().name());
                dag = new DAGImpl(rootCommit);
            }

            System.out.println("-------------------------------------");
            System.out.println("Number of commits in branch: " + count);

            walk.dispose();
        }
    }

    private void processPreviousCommits(RevCommit rev, String currentCommitId) {

        List<Commit> parents = new LinkedList<>();
        for(RevCommit prev:rev.getParents()) {
            String id = prev.getId().name();
            Commit prevCommit;
            if (commits.containsKey(id)) {
                prevCommit = commits.get(id);
            } else {
                prevCommit = new CommitImpl(id);
                commits.put(id, prevCommit);
            }
            parents.add(prevCommit);
        }

        commits.get(currentCommitId).addPrevious(parents);
    }

    private Tree processCommit(Repository repository, RevCommit commit) throws IOException {
        System.out.println("Commit: " + commit);
        System.out.println("\tMessage: " + commit.getShortMessage());

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

        ObjectLoader loader = repository.open(id);
        System.out.print("Blob: ");
        loader.copyTo(System.out);

        String blobSHA = id.getName();
        if (!blobs.containsKey(blobSHA)) {
            Blob blob = new BlobImpl(blobSHA, loader.openStream());
            blobs.put(blobSHA, blob);
        }

        return blobs.get(blobSHA);
    }

}
