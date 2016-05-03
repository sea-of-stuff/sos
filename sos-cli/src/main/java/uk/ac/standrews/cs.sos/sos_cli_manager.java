package uk.ac.standrews.cs;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.commands.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class sos_cli_manager {

    private final static String ADD_ATOM = "atom";
    private final static String ADD_COMPOUND = "compound";
    private final static String ADD_ASSET = "asset";
    private final static String SET_IDENTITY = "identity";
    private final static String FIND = "find";
    private final static String VERIFY = "verify"; // Manifest should be piped

    public sos_cli_manager(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();

        manageInput(parser, options, args);
    }

    private Options createOptions() {
        Options options = new Options();
        options.addOption(getAtomOption());
        options.addOption(getCompoundOption());
        options.addOption(getAssetOption());
        options.addOption(getFindOption());
        options.addOption(getVerifyOption());
        return options;
    }

    private Option getAtomOption() {
        return Option.builder(ADD_ATOM)
                .required(false)
                .numberOfArgs(2) // TODO - might have multiple locations
                .desc("Add atom to the sea of stuff")
                .build();
    }

    private Option getCompoundOption() {
        return Option.builder(ADD_COMPOUND)
                .required(false)
                .hasArgs()
                .desc("Add compound to the sea of stuff")
                .build();
    }

    private Option getAssetOption() {
        return Option.builder(ADD_ASSET)
                .required(false)
                .hasArgs()
                .desc("Add asset to the sea of stuff")
                .build();
    }

    private Option getFindOption() {
        return Option.builder(FIND)
                .required(false)
                .hasArgs()
                .desc("Find manifest in the sea of stuff")
                .build();
    }

    private Option getVerifyOption() {
        return Option.builder(VERIFY)
                .required(false)
                .hasArg()
                .desc("Verfiy a given manifest against the current set identity")
                .build();
    }

    private void manageInput(CommandLineParser parser, Options options, String[] args) {
        try {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption(ADD_ATOM)) {
                sos_cli_atom.AddAtom(line.getOptionValues(ADD_ATOM));
            } else if (line.hasOption(ADD_COMPOUND)) {
                sos_cli_compound.AddCompound(line.getOptionValues(ADD_COMPOUND));
            } else if (line.hasOption(ADD_ASSET)) {
                sos_cli_asset.AddAsset(line.getOptionValues(ADD_ASSET));
            } else if (line.hasOption(SET_IDENTITY)) {
                // TODO
            } else if (line.hasOption(FIND)) {
                sos_cli_find.GetManifest(line.getOptionValues(FIND));
            } else if (line.hasOption(VERIFY)) {
                sos_cli_verify.Verify(line.getOptionValue(VERIFY));
            }

        }
        catch(ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }
}
