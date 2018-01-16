package uk.ac.standrews.cs.sos.experiments.analyse_internals;

import uk.ac.standrews.cs.sos.utils.Misc;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AnalyseInternals {

    public static void analyseFilesInFolder(String path) {

        File folder = new File(path);
        File[] files = folder.listFiles();
        assert(files != null);

        for(File file : files) {
            long size = file.length();
            System.out.print("\t");
            printFolderSize(file.getName(), size);
        }
    }

    public static void printFolderSize(String folder, long size) {
        String padding = folder.length() > 10 ? "\t\t" : "\t\t\t";
        String retval = String.format("%s%s(in bytes): %d\t(in KB): %.2f\t(in MB): %.2f", folder, padding, size, Misc.toKB(size), Misc.toMB(size));
        System.out.println(retval);
    }
}
