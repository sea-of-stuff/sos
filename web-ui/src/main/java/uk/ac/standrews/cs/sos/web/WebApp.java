package uk.ac.standrews.cs.sos.web;

import spark.ModelAndView;
import spark.Request;
import spark.template.velocity.VelocityTemplateEngine;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class WebApp {

    public static void RUN(SOSLocalNode sos) {
        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        port(9999);

        get("/", (req, res) -> renderHome(req, sos));

        get("/hello", (req, res) -> "Hello World");

        after((req, res) -> {
            if (res.body() == null) { // if the route didn't return anything
                res.body(renderHome(req, sos));
            }
        });
    }

    private static String renderHome(Request req, SOSLocalNode sos) throws URISyntaxException, ManifestPersistException, StorageException, ManifestNotFoundException {
        Map<String, Object> model = new HashMap<>();
        model.put("manifests", sos.getClient().getAllManifests().toArray());

        return renderTemplate("velocity/index.vm", model);
    }

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }
}
