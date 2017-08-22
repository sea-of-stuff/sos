package uk.ac.standrews.cs.sos.web;

import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.sos.web.agents.*;

import static spark.Spark.*;
import static uk.ac.standrews.cs.sos.rest.RESTConfig.sos;

public class WebApp {

    public static final int DATA_LIMIT = 256;

    public static void RUN(SOSLocalNode sos, IFileSystem fileSystem, int port) {

        SOS_LOG.log(LEVEL.INFO, "Starting WEB APP on port: " + port);

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        port(port);

        staticFiles.location("/velocity");
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
        get("/settings", (req, res) -> WSettings.Render(sos));
        post("/version", (req, res) -> WData.AddAtomVersion(req, sos));
        post("/version/protected/:roleid", (req, res) -> WData.AddProtectedAtomVersion(req, sos));
        post("/updateVersion/:prev", (req, res) -> WData.UpdateAtomVersion(req, sos));

        get("/data/:id", (req, res) -> WData.Render(req, sos));
        get("/manifest/:id", (req, res) -> WManifest.Render(req, sos));
        get("/metadata/:id", (req, res) -> WMetadata.Render(req, sos));

        get("/webdav", (req, res) -> WWebDAV.Render(sos, fileSystem));
        get("/nodes", (req, res) -> WNodes.Render(sos));

        get("/contexts", (req, res) -> WContexts.Render(sos));
        post("/contexts", (req, res) -> WContexts.CreateContext(req, res, sos));
        post("/preview", (req, res) -> WContexts.PreviewClassContext(req, res));
        get("/context/:id/contents", (req, res) -> WContexts.GetContents(req, sos)); // TODO - further testing

        get("/usro", (req, res) -> WUsersRoles.Render(sos));
        post("/usro/user", (req, res) -> WUsersRoles.CreateUser(req, res, sos));
        post("/usro/role", (req, res) -> WUsersRoles.CreateRole(req, res, sos));

        get("/graph/:id", (req, res) -> WGraph.RenderPartial(req, sos));

        get("/verifySignature/:id", (req, res) -> WVerify.Render(req, sos)); // TODO - further testing
    }

    private static void registerPostActionRoutes() {
        after((req, res) -> {
            if (res.body() == null) { // if the route didn't return anything
                res.body(WHome.Render(sos));
            }
        });
    }

}
