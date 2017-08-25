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

        // PAGES
        get("/", (req, res) -> WHome.Render(sos));
        get("/index", (req, res) -> WHome.Render(sos));
        get("/webdav", (req, res) -> WWebDAV.Render(sos, fileSystem));
        get("/nodes", (req, res) -> WNodes.Render(sos));
        get("/webarchive", (req, res) -> WWebArchive.Render());
        get("/settings", (req, res) -> WSettings.Render(sos));
        get("/contexts", (req, res) -> WContexts.Render(sos));
        get("/usro", (req, res) -> WUsersRoles.Render(sos));

        // ACTIONS
        post("/version", (req, res) -> WData.AddAtomVersion(req, sos));
        post("/version/protected/:roleid", (req, res) -> WData.AddProtectedAtomVersion(req, sos));
        post("/version/sign/:roleidSign", (req, res) -> WData.AddSignedAtomVersion(req, sos));
        post("/version/protected/:roleid/sign/:roleidSign", (req, res) -> WData.AddProtectedAndSignedAtomVersion(req, sos));
        post("/updateVersion/prev/:prev", (req, res) -> WData.UpdateAtomVersion(req, sos));

        get("/data/:id", (req, res) -> WData.GetData(req, sos));
        get("/data/:id/download", (req, res) -> WData.GetDataDownload(req, res, sos));
        get("/data/:id/role/:roleid", (req, res) -> WData.GetProtectedData(req, sos));
        get("/data/:id/role/:roleid/download", (req, res) -> WData.GetProtectedDataDownload(req, res, sos));
        get("/manifest/:id", (req, res) -> WManifest.Render(req, sos));
        get("/metadata/:id", (req, res) -> WMetadata.Render(req, sos));

        post("/contexts", (req, res) -> WContexts.CreateContext(req, res, sos));
        post("/preview", (req, res) -> WContexts.PreviewClassContext(req, res));
        get("/context/:id/contents", (req, res) -> WContexts.GetContents(req, sos));

        post("/usro/user", (req, res) -> WUsersRoles.CreateUser(req, res, sos));
        post("/usro/role", (req, res) -> WUsersRoles.CreateRole(req, res, sos));

        get("/graph/:id", (req, res) -> WGraph.RenderPartial(req, sos));

        get("/verifySignature/:id/role/:roleid", (req, res) -> WVerify.VerifySignature(req, sos));
        get("/verifyIntegrity/:id", (req, res) -> WVerify.VerifyIntegrity(req, sos));

        get("/threads", (req, res) -> WContexts.Threads(sos));
    }

    private static void registerPostActionRoutes() {
        after((req, res) -> {
            if (res.body() == null) { // if the route didn't return anything
                res.body(WHome.Render(sos));
            }
        });
    }

}
