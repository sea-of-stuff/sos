package uk.ac.standrews.cs.sos.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import spark.ModelAndView;
import spark.Request;
import spark.template.velocity.VelocityTemplateEngine;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static spark.Spark.*;

public class WebApp {

    public static void RUN(SOSLocalNode sos, int port) {
        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        port(port);

        registerRoutes(sos);

        after((req, res) -> {
            if (res.body() == null) { // if the route didn't return anything
                res.body(renderHome(sos));
            }
        });
    }

    private static void registerRoutes(SOSLocalNode sos) {
        get("/", (req, res) -> renderHome(sos));
        get("/graph", (req, res) -> renderGraph(sos));
        get("/graph/data/:id", (req, res) -> renderData(req, sos));
        get("/hello", (req, res) -> "Hello World");

        post("/data", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            try (InputStream is = request.raw().getPart("atom").getInputStream()) {
                AtomBuilder builder = new AtomBuilder().setInputStream(is);
                Atom atom = sos.getClient().addAtom(builder);
            }
            return "File uploaded";
        });

    }

    private static String renderData(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, IOException {

        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);
        Manifest manifest = sos.getClient().getManifest(guid);

        if (manifest.getManifestType().equals("Atom")) {
            Atom atom = (Atom) manifest;

            return InputStreamToString(sos.getClient().getAtomContent(atom));
        }

        return null;
    }

    public static String InputStreamToString(InputStream string) throws IOException {
        return IOUtils.toString(string, StandardCharsets.UTF_8);
    }

    private static Object renderGraph(SOSLocalNode sos) {

        Map<String, Object> model = new HashMap<>();

        Object[] versions = sos.getClient().getAllManifests().
                filter(m -> m.getManifestType().equals("Version")).<Version>toArray();

        Object[] compounds = sos.getClient().getAllManifests().
                filter(m -> m.getManifestType().equals("Compound")).<Compound>toArray();

        Object[] atoms = sos.getClient().getAllManifests().
                filter(m -> m.getManifestType().equals("Atom")).<Atom>toArray();

        model.put("nodes", nodes(versions, compounds, atoms));
        model.put("edges", edges(versions, compounds));

        return renderTemplate("velocity/graph.vm", model);
    }

    private static String renderHome(SOSLocalNode sos) throws URISyntaxException, ManifestPersistException, StorageException, ManifestNotFoundException {
        Map<String, Object> model = new HashMap<>();
        model.put("versions", sos.getClient().getAllManifests().
                filter(m -> m.getManifestType().equals("Version")).<Version>toArray());

        model.put("compounds", sos.getClient().getAllManifests().
                filter(m -> m.getManifestType().equals("Compound")).<Compound>toArray());

        model.put("atoms", sos.getClient().getAllManifests().
                filter(m -> m.getManifestType().equals("Atom")).<Atom>toArray());

        return renderTemplate("velocity/index.vm", model);
    }

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }


    private static String nodes(Object[] versions, Object[] compounds, Object[] atoms) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for(Object v:versions) {
            Version version = (Version)v;

            ObjectNode objectNode1 = mapper.createObjectNode();
            objectNode1.put("id", version.getVersionGUID().toString());
            objectNode1.put("label", version.getVersionGUID().toString().substring(0, 5));
            objectNode1.put("group", version.getInvariantGUID().toString());
            objectNode1.put("shape", "circle");

            arrayNode.add(objectNode1);
        }

        for(Object c:compounds) {
            Compound compound = (Compound)c;
            ObjectNode objectNode1 = mapper.createObjectNode();
            objectNode1.put("id", compound.getContentGUID().toString());
            objectNode1.put("label", compound.getContentGUID().toString().substring(0, 5));
            objectNode1.put("group", compound.getManifestType());
            objectNode1.put("shape", "box");

            arrayNode.add(objectNode1);
        }

        for(Object a:atoms) {
            Atom atom = (Atom)a;
            ObjectNode objectNode1 = mapper.createObjectNode();
            objectNode1.put("id", atom.getContentGUID().toString());
            objectNode1.put("label", atom.getContentGUID().toString().substring(0, 5));
            objectNode1.put("group", atom.getManifestType());
            objectNode1.put("shape", "triangle");

            arrayNode.add(objectNode1);
        }

        return arrayNode.toString();
    }

    private static String edges(Object[] versions, Object[] compounds) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for(Object v:versions) {

            Version version = (Version)v;
            if (version.getPreviousVersions() != null && version.getPreviousVersions().size() > 0) {
                ObjectNode objectNode1 = mapper.createObjectNode();

                objectNode1.put("from", version.getVersionGUID().toString());
                objectNode1.put("to", version.getPreviousVersions().toArray()[0].toString());
                objectNode1.put("arrows", "to");

                arrayNode.add(objectNode1);

                ObjectNode objectNode2 = mapper.createObjectNode();
                objectNode2.put("from", version.getVersionGUID().toString());
                objectNode2.put("to", version.getContentGUID().toString());
                objectNode1.put("arrows", "to");
                objectNode2.put("physics", "false");

                arrayNode.add(objectNode2);
            }
        }

        for(Object c:compounds) {
            Compound compound = (Compound)c;

            IGUID compoundGUID = compound.getContentGUID();

            if (compound.getContents() != null && compound.getContents().size() > 0) {
                Iterator<Content> contents = compound.getContents().iterator();

                while(contents.hasNext()) {
                    Content content = contents.next();

                    ObjectNode objectNode1 = mapper.createObjectNode();

                    objectNode1.put("from", compoundGUID.toString());
                    objectNode1.put("to", content.getGUID().toString());
                    objectNode1.put("label", content.getLabel());
                    objectNode1.put("arrows", "to");

                    arrayNode.add(objectNode1);
                }

            }


        }

        return arrayNode.toString();

        /**
         * To make the JSON String pretty use the below code
         */
        // System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode));
    }


}
