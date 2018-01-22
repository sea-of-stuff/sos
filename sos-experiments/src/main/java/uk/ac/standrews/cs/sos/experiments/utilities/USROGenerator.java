package uk.ac.standrews.cs.sos.experiments.utilities;

import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.usro.RoleImpl;
import uk.ac.standrews.cs.sos.impl.usro.UserImpl;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class USROGenerator {

    public static void main(String[] args) throws SignatureException, UserNotFoundException, ProtectionException, ConfigurationException, FileNotFoundException {

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File("sos-experiments/src/main/resources/settings/settings.json")).getSettingsObj();
        SOSLocalNode.settings = settings;
        SOSLocalNode.settings.setKeys(new SettingsConfiguration.Settings.KeysSettings());
        SOSLocalNode.settings.getKeys().setLocation("experiments/usro/keys/");

        System.out.println("USRO Generator. Options: user, role");
        Scanner in = new Scanner(System.in);
        String option = in.nextLine().toLowerCase();
        switch(option) {
            case "user":
                System.out.println("Type name of the user:");
                String username = in.next();
                User user = new UserImpl(username);
                System.out.println(user.toString());
                stringToFile("experiments/usro/" + user.guid().toMultiHash() + ".json", user.toString());
                break;

            case "role":
                System.out.println("Available users/roles in experiments/usro:");

                File[] usroManifests = new File("experiments/usro/").listFiles();
                assert usroManifests != null;
                for (File srcFile: usroManifests) {
                    if (srcFile.isFile()) {
                        System.out.println("\t\t" + srcFile.getAbsolutePath());
                    }
                }

                System.out.println("File path for user:");
                String filepath = in.next();
                User retrievedUsed = FileUtils.UserFromFile(new File(filepath));

                System.out.println("Type name of the role:");
                String rolename = in.next();
                Role role = new RoleImpl(retrievedUsed, rolename);
                System.out.println(role.toString());
                stringToFile("experiments/usro/" + role.guid().toMultiHash() + ".json", role.toString());
                break;

            default:
                System.err.println("Unknown option: " + option);
        }


        System.out.println("-----------------");
        System.out.println("The user/role is saved in experiments/usro/");
        System.out.println("Find keys and certificates at the following path: experiments/usro/keys/");
    }

    private static void stringToFile(String filepath, String text) throws FileNotFoundException {

        try(PrintWriter out = new PrintWriter(filepath)){
            out.println(text);
        }
    }
}
