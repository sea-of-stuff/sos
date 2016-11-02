package uk.ac.standrews.cs.sos.web;

import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import javax.servlet.MultipartConfigElement;
import java.io.InputStream;

import static spark.Spark.*;

public class WebApp {

    public static void RUN(SOSLocalNode sos, int port) {
        System.out.println("Starting WEB APP on port: " + port);

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        port(port);

        registerRoutes(sos);

        after((req, res) -> {
            if (res.body() == null) { // if the route didn't return anything
                res.body(Home.Render());
            }
        });
    }

    private static void registerRoutes(SOSLocalNode sos) {
        get("/", (req, res) -> Home.Render());
        get("/graph", (req, res) -> Graph.Render(sos));
        get("/graph/data/:id", (req, res) -> Data.Render(req, sos));
        get("/graph/manifest/:id", (req, res) -> Manifest.Render(req, sos));
        get("/hello", (req, res) -> "Hello World");

        // TODO - refactor
        post("/data", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            try (InputStream is = request.raw().getPart("atom").getInputStream()) {
                AtomBuilder builder = new AtomBuilder().setInputStream(is);
                Atom atom = sos.getClient().addAtom(builder);
            }
            return "File uploaded";
        });

    }



}
