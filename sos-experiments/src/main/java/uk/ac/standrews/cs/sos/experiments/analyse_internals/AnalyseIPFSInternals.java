package uk.ac.standrews.cs.sos.experiments.analyse_internals;

import uk.ac.standrews.cs.sos.utils.FileUtils;

import java.nio.file.Paths;
import java.util.Scanner;

/**
 * See: https://ipfs.io/docs/
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AnalyseIPFSInternals extends AnalyseInternals {

    public static void main(String[] args) {

        System.out.println("Enter path to analyse:");
        System.out.println("\te.g., ~/.ipfs");
        Scanner in = new Scanner(System.in);
        String path = in.nextLine();
        path = path.replaceFirst("^~",System.getProperty("user.home"));
        analysePath(path);
    }

    private static void analysePath(String path) {

        long blocksSize = FileUtils.size(Paths.get(path + "/blocks"));
        printFolderSize("/blocks", blocksSize);

        long datastoreSize = FileUtils.size(Paths.get(path + "/datastore"));
        printFolderSize("/datastore", datastoreSize);

        long keystoreSize = FileUtils.size(Paths.get(path + "/keystore"));
        printFolderSize("/keystore", keystoreSize);
    }
}
