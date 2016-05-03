package uk.ac.standrews.cs.commands;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import uk.ac.standrews.cs.sos.exceptions.UnknownGUIDException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos_cli;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class sos_cli_verify {

    public static void Verify(String arg) throws ParseException {
        manageInput(arg);
    }

    private static void manageInput(String arg) {

        try {
            Identity identity = sos_cli.seaOfStuff.getIdentity();
            Manifest manifest = sos_cli.seaOfStuff.getManifest(new GUIDsha1(arg));
            boolean isVerified = sos_cli.seaOfStuff.verifyManifest(identity, manifest);
            System.out.println("Manifest with GUID " + arg + " has been verified: " + isVerified);
        } catch (UnknownGUIDException e) {
            e.printStackTrace();
        } catch (ManifestVerificationFailedException e) {
            e.printStackTrace();
        }

    }
}
