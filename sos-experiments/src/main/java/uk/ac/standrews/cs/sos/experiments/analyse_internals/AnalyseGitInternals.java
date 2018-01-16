package uk.ac.standrews.cs.sos.experiments.analyse_internals;

import org.eclipse.jgit.errors.InvalidObjectIdException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.Misc;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import static org.eclipse.jgit.lib.Constants.*;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AnalyseGitInternals extends AnalyseInternals {

    // Neeed to avoid error thrown from jgit conflicting with SOS log
    static {
        new SOS_LOG(GUIDFactory.generateRandomGUID(GUID_ALGORITHM));
    }

    public static void main(String[] args) {

        System.out.println("Enter path to analyse:");
        System.out.println("\te.g., ~/git/sos");
        Scanner in = new Scanner(System.in);
        String path = in.nextLine();
        path = path.replaceFirst("^~",System.getProperty("user.home"));
        analysePath(path);
    }

    private static void analysePath(String path) {

        long gitSize = FileUtils.size(Paths.get(path + "/.git"));
        printFolderSize("/.git", gitSize);

        long objectsSize = FileUtils.size(Paths.get(path + "/.git/objects"));
        printFolderSize("/.git/objects", objectsSize);
        analyseGitObjects(path);

        long refsSize = FileUtils.size(Paths.get(path + "/.git/refs"));
        printFolderSize("/.git/refs", refsSize);

        long logsSize = FileUtils.size(Paths.get(path + "/.git/logs"));
        printFolderSize("/.git/logs", logsSize);

        long lfsSize = FileUtils.size(Paths.get(path + "/.git/lfs"));
        printFolderSize("/.git/lfs", lfsSize);
    }


    private static void analyseGitObjects(String path) {

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try (Repository repository = builder.setGitDir(new File(path + "/.git"))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build()) {

            GitInfo gitInfo = typeToSize(repository, new File(path + "/.git/objects").toPath());
            System.out.println("\tNumber of Objects: " + gitInfo.numberObjects);
            System.out.println("\tNumber of Non-Valid Objects: " + gitInfo.nonValidObjects);
            System.out.println("\t_________________________________________________");
            for(Map.Entry<String, Long> entry:gitInfo.typeSize.entrySet()) {
                String retval = String.format("\t%s\t(in bytes): %d\t(in KB): %.2f\t(in MB): %.2f", entry.getKey(), entry.getValue(), Misc.toKB(entry.getValue()), Misc.toMB(entry.getValue()));
                System.out.println(retval);
            }
            System.out.println("\t_________________________________________________");
            for(Map.Entry<String, Long> entry:gitInfo.fileSize.entrySet()) {
                String retval = String.format("\t%s\t(in bytes): %d\t(in KB): %.2f\t(in MB): %.2f", entry.getKey(), entry.getValue(), Misc.toKB(entry.getValue()), Misc.toMB(entry.getValue()));
                System.out.println(retval);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static GitInfo typeToSize(Repository repository, Path path) {

        GitInfo gitInfo = new GitInfo();

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    try {
                        String sha = file.getParent().getFileName().toString() + file.getFileName().toString();
                        ObjectLoader objectLoader = repository.getObjectDatabase().open(ObjectId.fromString(sha));
                        String type = getType(objectLoader.getType());
                        long size = attrs.size();

                        if (!gitInfo.typeSize.containsKey(type)) {
                            gitInfo.typeSize.put(type, size);
                        } else {
                            long prevLength = gitInfo.typeSize.get(type);
                            gitInfo.typeSize.put(type, prevLength + size);
                        }

                        gitInfo.numberObjects++;

                        return FileVisitResult.CONTINUE;
                    } catch (IOException | InvalidObjectIdException e) {
                        gitInfo.nonValidObjects++;

                        String fileName = file.getFileName().toString();
                        long size = attrs.size();

                        if (!gitInfo.fileSize.containsKey(fileName)) {
                            gitInfo.fileSize.put(fileName, size);
                        } else {
                            long prevLength = gitInfo.fileSize.get(fileName);
                            gitInfo.fileSize.put(fileName, prevLength + size);
                        }

                        return FileVisitResult.CONTINUE;
                    }

                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {

                    System.out.println("skipped: " + file + " (" + exc + ")");
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                    if (exc != null)
                        System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return gitInfo;
    }

    private static class GitInfo {

        HashMap<String, Long> typeSize = new LinkedHashMap<>();
        HashMap<String, Long> fileSize = new LinkedHashMap<>();
        int numberObjects = 0;
        int nonValidObjects = 0;
    }

    private static String getType(int type) {

        switch(type) {
            case OBJ_COMMIT:
                return TYPE_COMMIT;
            case OBJ_TREE:
                return TYPE_TREE;
            case OBJ_BLOB:
                return TYPE_BLOB;
            case OBJ_TAG:
                return TYPE_TAG;
        }

        return "n/a";
    }


}
