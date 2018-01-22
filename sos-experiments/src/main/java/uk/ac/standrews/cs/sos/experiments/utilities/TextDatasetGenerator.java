package uk.ac.standrews.cs.sos.experiments.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TextDatasetGenerator {

    /*
     * Params:
     * - number of files
     * - min-max file size
     * - words in file with frequency
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        Scanner in = new Scanner(System.in);

        System.out.println("Dataset name:");
        String name = in.nextLine();

        Scanner datasetDefinition = new Scanner(new File("sos-experiments/src/main/resources/synthetic_datasets/" + name));
        String datasetPath = datasetDefinition.next();
        int numberOfFiles = datasetDefinition.nextInt();
        String[] fileSizeRange = datasetDefinition.next().split("-");
        int minFileSize = Integer.parseInt(fileSizeRange[0].trim());
        int maxFileSize = Integer.parseInt(fileSizeRange[1].trim());

        HashMap<String, Integer> wordsAndFrequency = new LinkedHashMap<>();
        while(datasetDefinition.hasNext()) {

            String[] wordFreq = datasetDefinition.next().split("-");
            String word = wordFreq[0].trim();
            int freq = Integer.parseInt(wordFreq[1].trim());

            wordsAndFrequency.put(word, freq);
        }

        createDataset(datasetPath, numberOfFiles, minFileSize, maxFileSize, wordsAndFrequency);
    }

    private static void createDataset(String datasetPath, int numberOfFiles, int minFileSize, int maxFileSize, HashMap<String, Integer> words) throws FileNotFoundException, UnsupportedEncodingException {

        ArrayList<String> dictionary = loadDictionary();
        new File(datasetPath).mkdirs();
        for(int i = 0; i < numberOfFiles; i++) {
            createFile(datasetPath + "/file_" + i, minFileSize, maxFileSize, dictionary, words);
        }
    }

    private static void createFile(String filepath, int minFileSize, int maxFileSize, ArrayList<String> dictionary, HashMap<String, Integer> words) throws FileNotFoundException, UnsupportedEncodingException {

        int numberOfWords = 0;
        int currentSize = 0;
        int expectedSize = ThreadLocalRandom.current().nextInt(minFileSize, maxFileSize + 1);

        try (PrintWriter writer = new PrintWriter(filepath, "UTF-8")) {

            while(true) {

                numberOfWords++;
                String wordToAdd = pickWord(dictionary, words);
                int wordLength = wordToAdd.length();

                if ((currentSize + wordLength) >= expectedSize) {
                    int truncationLength = expectedSize - currentSize;
                    wordToAdd = wordToAdd.substring(0, truncationLength);
                    writer.print(wordToAdd);

                    currentSize += truncationLength;
                    break;
                }

                currentSize += wordLength;
                writer.print(wordToAdd);
            }
        }

        System.out.println("Created file: " + filepath + " || Size (bytes): " + currentSize + " || No. words: " + numberOfWords) ;
    }

    private static String pickWord(ArrayList<String> dictionary, HashMap<String, Integer> words) {

        for(Map.Entry<String, Integer> entry:words.entrySet()) {

            int pickedVal = ThreadLocalRandom.current().nextInt(0, entry.getValue() + 1);
            if (pickedVal == entry.getValue()) return entry.getKey() + " ";
        }

        int indexPos = ThreadLocalRandom.current().nextInt(0, dictionary.size());
        return dictionary.get(indexPos) + " ";
    }

    private static ArrayList<String> loadDictionary() throws FileNotFoundException {
        String dictionaryPath = "third-party/english-words/words.txt";
        Scanner dictionaryScanner = new Scanner(new File(dictionaryPath));

        ArrayList<String> words = new ArrayList<>();
        while(dictionaryScanner.hasNext()) {
            words.add(dictionaryScanner.next());
        }

        return words;
    }

}
