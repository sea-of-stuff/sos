package uk.ac.standrews.cs.sos.web;

import spark.Request;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.graph.Data;
import uk.ac.standrews.cs.sos.web.graph.Graph;
import uk.ac.standrews.cs.sos.web.graph.Manifest;
import uk.ac.standrews.cs.sos.web.home.Home;
import uk.ac.standrews.cs.sos.web.tree.Tree;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

import static spark.Spark.*;

public class WebApp {

    public static void RUN(SOSLocalNode sos, IGUID root, int port) {
        System.out.println("Starting WEB APP on port: " + port);

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        port(port);

        registerRoutes(sos, root);
        registerPostActionRoutes();
    }

    private static void registerRoutes(SOSLocalNode sos, IGUID root) {
        get("/", (req, res) -> Home.Render());

        get("/tree", (req, res) -> Tree.Render(sos, root));

        get("/graph", (req, res) -> Graph.Render(sos));
        get("/graph/data/:id", (req, res) -> Data.Render(req, sos));
        get("/graph/manifest/:id", (req, res) -> Manifest.Render(req, sos));

        post("/data", (req, res) -> postData(req, sos));
    }

    private static void registerPostActionRoutes() {
        after((req, res) -> {
            if (res.body() == null) { // if the route didn't return anything
                res.body(Home.Render());
            }
        });
    }

    private static String postData(Request request, SOSLocalNode sos) throws IOException, ServletException, ManifestPersistException, StorageException {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

        try (InputStream is = request.raw().getPart("atom").getInputStream()) {
            AtomBuilder builder = new AtomBuilder().setInputStream(is);
            Atom atom = sos.getClient().addAtom(builder);
        }
        return "File uploaded";
    }

}
