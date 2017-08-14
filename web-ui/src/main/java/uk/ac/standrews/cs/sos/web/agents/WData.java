package uk.ac.standrews.cs.sos.web.agents;

import org.apache.xmlbeans.impl.util.Base64;
import spark.Request;
import uk.ac.standrews.cs.castore.data.ByteData;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Version;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static uk.ac.standrews.cs.sos.web.WebApp.DATA_LIMIT;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WData {

    public static String Render(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, IOException, MetadataNotFoundException, AtomNotFoundException {

        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);
        Manifest manifest = sos.getAgent().getManifest(guid);

        if (manifest.getType().equals(ManifestType.VERSION)) {
            Version version = (Version) manifest;
            Manifest contentManifest = sos.getDDS().getManifest(version.getContentGUID());
            if (contentManifest.getType().equals(ManifestType.ATOM)) {

                return GetData(sos, version, false);
            }
        }

        return "N/A";
    }

    public static String AddAtom(Request request, SOSLocalNode sos) {

        String contentType = request.headers("Content-Type");
        String delimiter = "--" + contentType.substring(contentType.indexOf("boundary=") + "boundary=".length());
        byte[] out = processPOSTData(request.bodyAsBytes(), delimiter);

        AtomBuilder atomBuilder = new AtomBuilder().setData(new ByteData(out));
        VersionBuilder versionBuilder = new VersionBuilder().setAtomBuilder(atomBuilder);

        sos.getAgent().addData(versionBuilder);

        return "";
    }

    private static byte[] processPOSTData(byte[] data, String delimiter) {

        int bodyIndex = 0;
        for(int i = 0; i < data.length; i++) {
            // 13 --> \r
            // 10 --> \n
            if (data[i] == (byte) 13 && data[i+1] == (byte) 10 && data[i+2] == (byte) 13 && data[i+3] == (byte) 10) {
                bodyIndex = i + 4;
                break;
            }
        }

        int endDelimiterLength = 2 /* \r\n */ + ("--" + delimiter + "--").length();
        byte[] out = new byte[data.length - bodyIndex - endDelimiterLength];
        for(int i = bodyIndex, j = 0; j < out.length; i++, j++) {
            out[j] = data[i];
        }

        return out;
    }

    public static String GetData(SOSLocalNode sos, Version version, boolean thumbnail) throws AtomNotFoundException {

        Data data = sos.getStorage().getAtomContent(version.getContentGUID());

        String type = "Raw";
        try {
            if (version.getMetadata() != null && !version.getMetadata().isInvalid()) {
                Metadata metadata = sos.getMMS().getMetadata(version.getMetadata());
                type = metadata.getPropertyAsString("Content-Type");
            }
        } catch (MetadataNotFoundException ignored) { }

        return GetData(type, data, thumbnail);
    }

    private static String GetData(String type, Data data, boolean thumbnail) {

        String outputData = "Cannot render this data type";

        switch (type) {
            case "Raw":
            case "application/octet-stream":
            case "multipart/appledouble":
            case "text/plain":
            case "text/plain; charset=ISO-8859-1":
                outputData = "<pre style=\"white-space: pre-wrap; word-wrap: break-word;\">";
                outputData += (data.toString().length() > DATA_LIMIT ? data.toString().substring(0, DATA_LIMIT) + ".... OTHER DATA FOLLOWING" : data.toString());
                outputData += "</pre>";
                break;
            case "image/png":
            case "image/jpeg":
            case "image/jpg":
            case "image/gif":
                byte b[] = data.getState();
                byte[] encodeBase64 = Base64.encode(b);
                String encodedData = new String(encodeBase64, StandardCharsets.UTF_8);

                int width = thumbnail ? 100 : 500;
                outputData = "<img style=\"max-width:" + width+ "px;\" src=\"data:" + type + ";base64," + encodedData + "\">";
                break;
        }

        return outputData;
    }

}
