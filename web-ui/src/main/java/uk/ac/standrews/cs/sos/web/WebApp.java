package uk.ac.standrews.cs.sos.web;

import spark.Request;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.graph.WData;
import uk.ac.standrews.cs.sos.web.graph.WGraph;
import uk.ac.standrews.cs.sos.web.graph.WManifest;
import uk.ac.standrews.cs.sos.web.graph.WMetadata;
import uk.ac.standrews.cs.sos.web.home.WHome;
import uk.ac.standrews.cs.sos.web.tree.WTree;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

import static spark.Spark.*;

public class WebApp {

    public static void RUN(SOSLocalNode sos, IFileSystem fileSystem, int port) {
        System.out.println("Starting WEB APP on port: " + port);

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        port(port);

        registerRoutes(sos, fileSystem);
        registerPostActionRoutes();
    }

    private static void registerRoutes(SOSLocalNode sos, IFileSystem fileSystem) {
        get("/", (req, res) -> WHome.Render());

        get("/tree", (req, res) -> WTree.Render(sos, fileSystem));

        get("/graph/:id", (req, res) -> WGraph.RenderPartial(req, sos));
        get("/graph/data/:id", (req, res) -> WData.Render(req, sos));
        get("/graph/manifest/:id", (req, res) -> WManifest.Render(req, sos));
        get("/metadata/:id", (req, res) -> WMetadata.Render(req, sos));

        post("/data", (req, res) -> postData(req, sos));
    }

    private static void registerPostActionRoutes() {
        after((req, res) -> {
            if (res.body() == null) { // if the route didn't return anything
                res.body(WHome.Render());
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
