package uk.ac.standrews.cs.sos.experiments.utilities;

import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class KeyGenerator {

    public static void main(String[] args) throws CryptoException, FileNotFoundException {

        System.out.println("K to generate key and E to encrypt password/passphrase");

        Scanner in = new Scanner(System.in);
        String option = in.nextLine();
        switch(option) {
            case "k": case "K":
                SecretKey key = SymmetricEncryption.generateRandomKey();

                File keyFile = new File(".key");
                try (PrintWriter printWriter = new PrintWriter(keyFile)) {
                    printWriter.write(SymmetricEncryption.keyToString(key));
                }
                break;

            case "e": case "E":
                System.out.println("Input the password/passphrase");
                String pass = in.nextLine();

                String content = new Scanner(new File(".key")).useDelimiter("\\Z").next();
                String encryptedPass = SymmetricEncryption.encrypt(SymmetricEncryption.getKey(content), pass);
                encryptedPass = encryptedPass.replace("\n", "").replace("\r", "");
                System.out.println("Encrypted key is: " + encryptedPass);
                break;
                default:
                    System.out.println("Option unknown");
        }

    }

    public static String pass(String encryptedPass) throws CryptoException, FileNotFoundException {

        String content = new Scanner(new File(".key")).useDelimiter("\\Z").next();
        return SymmetricEncryption.decrypt(SymmetricEncryption.getKey(content), encryptedPass);
    }
}
