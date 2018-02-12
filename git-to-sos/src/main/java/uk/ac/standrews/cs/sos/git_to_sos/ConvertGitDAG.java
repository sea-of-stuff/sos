package uk.ac.standrews.cs.sos.git_to_sos;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

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
public class ConvertGitDAG {

    private static DAG dag;
    private static HashMap<String, Commit> commits = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException, GitAPIException {

        String path = "src/main/resources/test-git";
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

    private static void walkBranch(Repository repository, Ref branchRef) throws IOException {

        System.out.println("-------------------------------------");
        System.out.println("-------------------------------------");
        System.out.println("Branch: " + branchRef + " " + branchRef.getName() + " SHA1: " + branchRef.getObjectId().getName());

        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(branchRef.getObjectId());
            System.out.println("Start-Commit: " + commit);
            System.out.println("-------------------------------------");
            walk.markStart(commit);
            int count = 0;
            RevCommit currentRev = null;
            for (RevCommit rev : walk) {
                currentRev = rev;
                Commit currentCommit = new CommitImpl(currentRev.getId().name());
                commits.put(currentCommit.getId(), currentCommit);
                processPreviousCommits(currentRev, currentCommit);

                getFilesInCommit(repository, rev);
                count++;
            }

            if (currentRev != null && branchRef.getName().equals("refs/heads/master")) {
                Commit rootCommit = new CommitImpl(currentRev.getId().name());
                dag = new DAGImpl(rootCommit);
            }

            System.out.println("-------------------------------------");
            System.out.println("Number of commits in branch: " + count);

            walk.dispose();
        }
    }

    private static void processPreviousCommits(RevCommit rev, Commit commit) {

        List<Commit> parents = new LinkedList<>();
        for(RevCommit prev:rev.getParents()) {
            String id = prev.getId().name();
            Commit prevCommit = commits.containsKey(id) ? commits.get(id) : new CommitImpl(id);
            parents.add(prevCommit);
        }
        commit.addPrevious(parents);
    }

    private static void getFilesInCommit(Repository repository, RevCommit commit) throws IOException {
        System.out.println("Commit: " + commit);

        RevTree tree = commit.getTree();
        System.out.println("Tree: " + tree);

        // now use a TreeWalk to iterate over all files in the Tree recursively
        // you can set Filters to narrow down the results if needed
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            int idx = 0;
            while (treeWalk.next()) {
                ObjectId objId = treeWalk.getObjectId(0);
                System.out.println("[ " + idx + " ] Blob path: " + treeWalk.getPathString() + " - SHA1: " + objId.getName());

                ObjectLoader objectLoader = repository.getObjectDatabase().open(objId);
                if (objectLoader.getType() == OBJ_BLOB) {
                    readBlob(repository, treeWalk.getObjectId(0));
                } else {
                    System.out.println("Object is not a blob");
                }
                idx++;
            }

        }

        System.out.println(); // print empty line
    }

    private static void readBlob(Repository repository, AnyObjectId id) throws IOException {

        ObjectLoader loader = repository.open(id);
        System.out.print("Blob: ");
        loader.copyTo(System.out);
    }

}
