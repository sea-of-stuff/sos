package uk.ac.standrews.cs.sos.experiments.utilities;

import org.bouncycastle.util.encoders.Base64;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.Scanner;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeGenerator {

    public static void main(String[] args) throws CryptoException, GUIDGenerationException {

        String path = "sos-experiments/src/main/resources/generated_nodes/";

        System.out.println("Node Generator. Input node name");
        Scanner in = new Scanner(System.in);
        String nodeName = in.nextLine();

        KeyPair keys = DigitalSignature.generateKeys();
        DigitalSignature.persist(keys, Paths.get(path + nodeName), Paths.get(path + nodeName));
        System.out.println("-------------------");
        System.out.println("Generated certificate and key for node " + nodeName + " at base path " + path + nodeName);
        System.out.println("One-line certificate to use in bootstrap node setting: " + new String(Base64.encode((keys.getPublic().getEncoded()))));

        IGUID guid = GUIDFactory.generateGUID(new ByteArrayInputStream(keys.getPublic().getEncoded()));
        System.out.println("-------------------");
        System.out.println("GUID of node is: " + guid.toMultiHash());

        System.out.println("-------------------");
        System.out.println("Find node ip by running 'ifconfig' and checking the inet IP address for the em1 interface (hogun cluster)");
    }
}
