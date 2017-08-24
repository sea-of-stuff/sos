package uk.ac.standrews.cs.sos.web.agents;

import org.apache.xmlbeans.impl.util.Base64;
import spark.Request;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.DataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.*;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.web.WebApp.DATA_LIMIT;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WData {

    public static String GetData(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, MetadataNotFoundException, AtomNotFoundException {

        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);
        Manifest manifest = sos.getAgent().getManifest(guid);

        if (manifest.getType().equals(ManifestType.VERSION)) {
            Version version = (Version) manifest;
            Manifest contentManifest = sos.getDDS().getManifest(version.getContentGUID());
            if (contentManifest.getType().equals(ManifestType.ATOM)) {

                return GetData(sos, version);
            }
        }

        return "N/A";
    }

    public static String GetProtectedData(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, MetadataNotFoundException, AtomNotFoundException, RoleNotFoundException {

        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);
        Manifest manifest = sos.getAgent().getManifest(guid);

        IGUID roleid = GUIDFactory.recreateGUID(req.params("roleid"));
        Role role = sos.getRMS().getRole(roleid);

        if (manifest.getType().equals(ManifestType.VERSION)) {
            Version version = (Version) manifest;
            Manifest contentManifest = sos.getDDS().getManifest(version.getContentGUID());
            if (contentManifest.getType().equals(ManifestType.ATOM_PROTECTED)) {

                try {
                    return GetProtectedData(sos, version, (SecureAtom) contentManifest, role);
                } catch (DataNotFoundException e) {

                    return "Unable to get Protected Data";
                }
            }
        }

        return "N/A";
    }

    public static String AddAtomVersion(Request request, SOSLocalNode sos) throws IOException, ServletException {

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        try (InputStream is = request.raw().getPart("file").getInputStream()) {
            // Use the input stream to create a file
            AtomBuilder atomBuilder = new AtomBuilder().setData(new InputStreamData(is));
            VersionBuilder versionBuilder = new VersionBuilder().setAtomBuilder(atomBuilder);

            sos.getAgent().addData(versionBuilder);
        }

        return "Atom Added";
    }

    public static String AddProtectedAtomVersion(Request request, SOSLocalNode sos) throws IOException, ServletException, GUIDGenerationException, RoleNotFoundException {

        IGUID roleid = GUIDFactory.recreateGUID(request.params("roleid"));
        Role role = sos.getRMS().getRole(roleid);

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        try (InputStream is = request.raw().getPart("file").getInputStream()) {
            // Use the input stream to create a file
            AtomBuilder atomBuilder = new AtomBuilder()
                    .setData(new InputStreamData(is))
                    .setRole(role);

            VersionBuilder versionBuilder = new VersionBuilder()
                    .setAtomBuilder(atomBuilder);

            sos.getAgent().addData(versionBuilder);
        }

        return "Atom Added";
    }

    public static String AddSignedAtomVersion(Request request, SOSLocalNode sos) throws IOException, ServletException, GUIDGenerationException, RoleNotFoundException {

        IGUID roleidSign = GUIDFactory.recreateGUID(request.params("roleidSign"));
        Role signer = sos.getRMS().getRole(roleidSign);

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        try (InputStream is = request.raw().getPart("file").getInputStream()) {
            // Use the input stream to create a file
            AtomBuilder atomBuilder = new AtomBuilder()
                    .setData(new InputStreamData(is));
            VersionBuilder versionBuilder = new VersionBuilder()
                    .setAtomBuilder(atomBuilder)
                    .setRole(signer);

            sos.getAgent().addData(versionBuilder);
        }

        return "Atom Added";
    }


    public static String AddProtectedAndSignedAtomVersion(Request request, SOSLocalNode sos) throws IOException, ServletException, GUIDGenerationException, RoleNotFoundException {

        IGUID roleid = GUIDFactory.recreateGUID(request.params("roleid"));
        Role role = sos.getRMS().getRole(roleid);

        IGUID roleidSign = GUIDFactory.recreateGUID(request.params("roleidSign"));
        Role signer = sos.getRMS().getRole(roleidSign);

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        try (InputStream is = request.raw().getPart("file").getInputStream()) {
            // Use the input stream to create a file
            AtomBuilder atomBuilder = new AtomBuilder()
                    .setData(new InputStreamData(is))
                    .setRole(role);

            VersionBuilder versionBuilder = new VersionBuilder()
                    .setAtomBuilder(atomBuilder)
                    .setRole(signer);

            sos.getAgent().addData(versionBuilder);
        }

        return "Atom Added";
    }

    public static Object UpdateAtomVersion(Request request, SOSLocalNode sos) throws GUIDGenerationException, IOException, ServletException, ManifestNotFoundException {

        IGUID prev = GUIDFactory.recreateGUID(request.params("prev"));
        Version version = (Version) sos.getDDS().getManifest(prev);

        Set<IGUID> prevs = new LinkedHashSet<>();
        prevs.add(prev);

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        try (InputStream is = request.raw().getPart("file").getInputStream()) {
            // Use the input stream to create a file
            AtomBuilder atomBuilder = new AtomBuilder().setData(new InputStreamData(is));
            VersionBuilder versionBuilder = new VersionBuilder().setAtomBuilder(atomBuilder)
                    .setInvariant(version.getInvariantGUID())
                    .setPrevious(prevs);

            sos.getAgent().addData(versionBuilder);
        }

        return "Atom Added";
    }

    public static String GetData(SOSLocalNode sos, Version version) throws AtomNotFoundException {

        Data data = sos.getStorage().getAtomContent(version.getContentGUID());

        String type = "Raw";
        try {
            if (version.getMetadata() != null && !version.getMetadata().isInvalid()) {
                Metadata metadata = sos.getMMS().getMetadata(version.getMetadata());
                type = metadata.getPropertyAsString("Content-Type");
            }
        } catch (MetadataNotFoundException ignored) { }

        return GetData(type, data);
    }

    public static String GetProtectedData(SOSLocalNode sos, Version version, SecureAtom atom, Role role) throws AtomNotFoundException, DataNotFoundException, ManifestNotFoundException {

        Data data = sos.getStorage().getSecureAtomContent(atom, role);

        String type = "Raw";
        try {
            if (version.getMetadata() != null && !version.getMetadata().isInvalid()) {
                Metadata metadata = sos.getMMS().getMetadata(version.getMetadata());
                type = metadata.getPropertyAsString("Content-Type");
            }
        } catch (MetadataNotFoundException ignored) { }

        return GetData(type, data);
    }

    private static String GetData(String type, Data data) {

        String outputData = "Cannot render this data type";

        switch (type) {
            case "Raw":
            case "application/octet-stream":
            case "multipart/appledouble":
            case "text/plain":
            case "text/plain; charset=ISO-8859-1":
            case "text/plain; charset=UTF-8":
            case "text/plain; charset=windows-1252":
                outputData = "<pre style=\"white-space: pre-wrap; word-wrap: break-word;\">";
                outputData += (data.toString().length() > DATA_LIMIT ? data.toString().substring(0, DATA_LIMIT) + " <br><strong>.... OTHER DATA FOLLOWING</strong>" : data.toString());
                outputData += "</pre>";
                break;
            case "image/png":
            case "image/jpeg":
            case "image/jpg":
            case "image/gif":
                byte b[] = data.getState();
                byte[] encodeBase64 = Base64.encode(b);
                String encodedData = new String(encodeBase64, StandardCharsets.UTF_8);
                outputData = "<img class=\"img-fluid\" src=\"data:" + type + ";base64," + encodedData + "\">";
                break;
        }

        return outputData;
    }

}
