package uk.ac.standrews.cs.sos.web;

import spark.Request;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.sos.web.contexts.WContexts;
import uk.ac.standrews.cs.sos.web.graph.*;
import uk.ac.standrews.cs.sos.web.home.WHome;
import uk.ac.standrews.cs.sos.web.nodes.WNodes;
import uk.ac.standrews.cs.sos.web.tree.WTree;
import uk.ac.standrews.cs.sos.web.usro.WUsersRoles;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

import static spark.Spark.*;
import static uk.ac.standrews.cs.sos.RESTConfig.sos;

public class WebApp {

    public static void RUN(SOSLocalNode sos, IFileSystem fileSystem, int port) {

        SOS_LOG.log(LEVEL.INFO, "Starting WEB APP on port: " + port);

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        port(port);

        staticFiles.location("/public");
        staticFiles.expireTime(0L);

        // Set up before-filters (called before each get/post)
        before("*", Filters.handleLocaleChange);

        registerRoutes(sos, fileSystem);
        registerPostActionRoutes();

        //Set up after-filters (called after each get/post)
        after("*", Filters.addGzipHeader);
    }

    private static void registerRoutes(SOSLocalNode sos, IFileSystem fileSystem) {
        get("/", (req, res) -> WHome.Render(sos));

        get("/tree", (req, res) -> WTree.Render(sos, fileSystem));
        get("/nodes", (req, res) -> WNodes.Render(sos));

        get("/contexts", (req, res) -> WContexts.Render(sos));
        post("/contexts", (req, res) -> WContexts.CreateContext(req, res, sos));
        post("/preview", (req, res) -> WContexts.PreviewClassContext(req, res));
        get("/context/:id/contents", (req, res) -> WContexts.GetContents(req, sos)); // TODO - further testing

        get("/usro", (req, res) -> WUsersRoles.Render(sos));
        post("/usro/user", (req, res) -> WUsersRoles.CreateUser(req, res, sos));
        post("/usro/role", (req, res) -> WUsersRoles.CreateRole(req, res, sos));

        get("/graph/:id", (req, res) -> WGraph.RenderPartial(req, sos));
        get("/graph/data/:id", (req, res) -> WData.Render(req, sos));
        get("/graph/manifest/:id", (req, res) -> WManifest.Render(req, sos));
        get("/metadata/:id", (req, res) -> WMetadata.Render(req, sos));
        get("/verifySignature/:id", (req, res) -> WVerify.Render(req, sos)); // TODO - further testing

        post("/data", (req, res) -> postData(req, sos));
    }

    private static void registerPostActionRoutes() {
        after((req, res) -> {
            if (res.body() == null) { // if the route didn't return anything
                res.body(WHome.Render(sos));
            }
        });
    }

    private static String postData(Request request, SOSLocalNode sos) throws IOException, ServletException, ManifestPersistException, StorageException {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

        try (InputStream is = request.raw().getPart("atom").getInputStream()) {
            AtomBuilder builder = new AtomBuilder().setInputStream(is);
            Atom atom = sos.getAgent().addAtom(builder);
        }
        return "CreateFile uploaded";
    }

}
