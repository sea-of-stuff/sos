package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.Scanner;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeGenerator {

    public static void main(String[] args) throws CryptoException {

        String path = "sos-experiments/src/main/resources/generated_nodes/";

        System.out.println("Node Generator. Input node name");
        Scanner in = new Scanner(System.in);
        String nodeName = in.nextLine();

        KeyPair keys = DigitalSignature.generateKeys();
        DigitalSignature.persist(keys, Paths.get(path + nodeName), Paths.get(path + nodeName));

        System.out.println("Generate certificate and key for node " + nodeName + " at base path " + path + nodeName);
    }
}
