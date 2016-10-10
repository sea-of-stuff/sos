package uk.ac.standrews.cs.sos.web;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import javax.servlet.MultipartConfigElement;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
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

    private static String renderHome(SOSLocalNode sos) throws URISyntaxException, ManifestPersistException, StorageException, ManifestNotFoundException {
        Map<String, Object> model = new HashMap<>();
        model.put("versions", sos.getClient().getAllManifests().
                filter(m -> m.getManifestType().equals("Version")).<Version>toArray());

        model.put("compounds", sos.getClient().getAllManifests().
                filter(m -> m.getManifestType().equals("Compound")).toArray());

        model.put("atoms", sos.getClient().getAllManifests().
                filter(m -> m.getManifestType().equals("Atom")).toArray());

        return renderTemplate("velocity/index.vm", model);
    }

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }

}
