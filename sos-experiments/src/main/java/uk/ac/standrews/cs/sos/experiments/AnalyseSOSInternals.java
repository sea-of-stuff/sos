package uk.ac.standrews.cs.sos.experiments;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.Misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AnalyseSOSInternals {

    public static void main(String[] args) {

        System.out.println("Enter path to analyse:");
        System.out.println("\te.g., ~/sos");
        Scanner in = new Scanner(System.in);
        String path = in.nextLine();
        path = path.replaceFirst("^~",System.getProperty("user.home"));
        analysePath(path);
    }

    private static void analysePath(String path) {

        long javaSize = FileUtils.size(Paths.get(path + "/java"));
        printFolderSize("sos/java", javaSize);

        long nodeSize = FileUtils.size(Paths.get(path + "/node"));
        printFolderSize("sos/node", nodeSize);
        analyseFilesInFolder(path + "/node");

        long keysSize = FileUtils.size(Paths.get(path + "/keys"));
        printFolderSize("sos/keys", keysSize);

        long dataSize = FileUtils.size(Paths.get(path + "/data"));
        printFolderSize("sos/data", dataSize);

        long manifestsSize = FileUtils.size(Paths.get(path + "/manifests"));
        printFolderSize("sos/manifests", manifestsSize);
        analyseManifests(path + "/manifests");

        System.out.println("------------------------------------------------------");

        System.out.println("Ratio data:manifests --> " + Misc.ratio(dataSize, manifestsSize));
        System.out.println("Ratio node:manifests --> " + Misc.ratio(nodeSize, manifestsSize));
    }

    private static void analyseManifests(String path) {

        HashMap<String, Long> typeToSize = new LinkedHashMap<>();
        File manifestsFolder = new File(path);
        File[] manifestFiles = manifestsFolder.listFiles();
        assert(manifestFiles != null);

        for(File manifestFile : manifestFiles) {

            try {
                JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(manifestFile);
                String type = jsonNode.get(JSONConstants.KEY_TYPE).textValue();

                long fileSize = manifestFile.length();
                if (!typeToSize.containsKey(type)) {
                    typeToSize.put(type, fileSize);
                } else {
                    long prevLength = typeToSize.get(type);
                    typeToSize.put(type, prevLength + fileSize);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        for(Map.Entry<String, Long> entry:typeToSize.entrySet()) {
            String retval = String.format("\t%s\t(in bytes): %d\t(in KB): %.2f\t(in MB): %.2f", entry.getKey(), entry.getValue(), Misc.toKB(entry.getValue()), Misc.toMB(entry.getValue()));
            System.out.println(retval);
        }
    }

    private static void analyseFilesInFolder(String path) {

        File folder = new File(path);
        File[] files = folder.listFiles();
        assert(files != null);

        for(File file : files) {
            long size = file.length();
            System.out.print("\t");
            printFolderSize(file.getName(), size);
        }
    }

    private static void printFolderSize(String folder, long size) {
        String padding = folder.length() > 10 ? "\t\t" : "\t\t\t";
        String retval = String.format("%s%s(in bytes): %d\t(in KB): %.2f\t(in MB): %.2f", folder, padding, size, Misc.toKB(size), Misc.toMB(size));
        System.out.println(retval);
    }
}
