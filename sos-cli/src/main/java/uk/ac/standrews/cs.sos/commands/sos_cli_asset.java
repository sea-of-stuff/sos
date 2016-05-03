package uk.ac.standrews.cs.commands;

import org.apache.commons.cli.*;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestSaveException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos_cli;
import uk.ac.standrews.cs.utility.Helper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class sos_cli_asset {

    private final static String CONTENT = "c";
    private final static String METADATA = "m";
    private final static String PREVIOUS = "p";
    private final static String INCARNATION = "i";

    public static void AddAsset(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        manageInput(parser, options, args);
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption(getAssetIncarnationOption());
        options.addOption(getAssetContentOption());
        options.addOption(getAssetMetadataOption());
        options.addOption(getAssetPreviousOption());
        return options;
    }

    private static Option getAssetIncarnationOption() {
        return Option.builder(INCARNATION)
                .required(false)
                .numberOfArgs(1)
                .desc("Incarnation GUID of the asset")
                .build();
    }

    private static Option getAssetContentOption() {
        return Option.builder(CONTENT)
                .required(true)
                .numberOfArgs(1)
                .desc("Contents of the asset")
                .build();
    }

    private static Option getAssetMetadataOption() {
        return Option.builder(METADATA)
                .required(false)
                .hasArgs()
                .desc("Metadata of the asset")
                .build();
    }

    private static Option getAssetPreviousOption() {
        return Option.builder(PREVIOUS)
                .required(false)
                .hasArgs()
                .desc("Previous versions of the asset")
                .build();
    }

    private static void manageInput(CommandLineParser parser, Options options, String[] args) {
        try {
            CommandLine line = parser.parse(options, args);

            Content content = getContent(line);
            GUID incarnation = getIncarnation(line);
            Collection<GUID> metadata = getMetadata(line);
            Collection<GUID> previous = getPrevious(line);

            Manifest manifest = sos_cli.seaOfStuff.addAsset(content, incarnation, previous, metadata);
            System.out.println("Version GUID: " + ((AssetManifest) manifest).getVersionGUID());
        } catch (ParseException exp) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        } catch (ManifestSaveException e) {
            System.out.println("Manifest could not be saved");
        } catch (ManifestNotMadeException e) {
            System.out.println("Manifest could not be generated");
        }
    }

    private static Content getContent(CommandLine line) {
        Content content = null;
        if(line.hasOption(CONTENT)) {
            String contentString = line.getOptionValue(CONTENT);
            content = Helper.getContent(contentString);
        }
        return content;
    }

    private static GUID getIncarnation(CommandLine line) {
        GUID incarnation = null;
        if(line.hasOption(INCARNATION)) {
            String incarnationString = line.getOptionValue(INCARNATION);
            incarnation = new GUIDsha1(incarnationString);
        }
        return incarnation;
    }

    private static Collection<GUID> getMetadata(CommandLine line) {
        Collection<GUID> metadata = new ArrayList<>();
        if (line.hasOption(METADATA)) {
            String[] metadataStrings = line.getOptionValues(METADATA);
            for(int i = 0; i < metadataStrings.length; i++) {
                metadata.add(new GUIDsha1(metadataStrings[i]));
            }
        }
        return metadata;
    }

    private static Collection<GUID> getPrevious(CommandLine line) {
        Collection<GUID> previous = new ArrayList<>();
        if (line.hasOption(PREVIOUS)) {
            String[] previousStrings = line.getOptionValues(PREVIOUS);
            for(int i = 0; i < previousStrings.length; i++) {
                previous.add(new GUIDsha1(previousStrings[i]));
            }
        }
        return previous;
    }

}
