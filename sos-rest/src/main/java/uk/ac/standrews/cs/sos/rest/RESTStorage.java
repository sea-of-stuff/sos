package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.actors.protocol.DDSNotificationInfo;
import uk.ac.standrews.cs.sos.bindings.StorageNode;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.Storage;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.json.model.LocationModel;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.sos.utils.Tuple;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/storage/")
@StorageNode
public class RESTStorage {

    @GET
    @Path("/data/guid/{guid}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getData(@PathParam("guid") String guid) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /storage/data/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID atomGUID;
        try {
            atomGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        Storage storage = RESTConfig.sos.getStorage();
        InputStream inputStream;
        try {
            inputStream = storage.getAtomContent(atomGUID);
        } catch (AtomNotFoundException e) {
            return HTTPResponses.NOT_FOUND("Atom not found");
        }

        return HTTPResponses.OK(inputStream);
    }

    @POST
    @Path("/uri")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postDataByLocation(LocationModel locationModel) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /storage/uri");

        if (locationModel == null) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        Storage storage = RESTConfig.sos.getStorage();

        Location location;
        try {
            location = locationModel.getLocation();
        } catch (IOException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        try {
            AtomBuilder builder = new AtomBuilder().setLocation(location);
            Tuple<Atom, Set<Node>> tuple = storage.addAtom(builder, true, new DDSNotificationInfo());

            String response = JSONResponse(tuple);
            return HTTPResponses.CREATED(response);
        } catch (StorageException | ManifestPersistException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

    }

    @POST
    @Path("/stream")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAtomStream(final InputStream inputStream) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /storage/stream");

        Storage storage = RESTConfig.sos.getStorage();

        Tuple<Atom, Set<Node>> tuple;
        try {
            AtomBuilder builder = new AtomBuilder().setInputStream(inputStream);
            tuple = storage.addAtom(builder, true, new DDSNotificationInfo()
                    .setNotifyDDSNodes(true)
                    .setUseDefaultDDSNodes(true)); // TODO - must be configurable from config file

        } catch (StorageException | ManifestPersistException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        String response = JSONResponse(tuple);
        return HTTPResponses.CREATED(response);
    }

    private String JSONResponse(Tuple<Atom, Set<Node>> tuple) {
        String retval = "{";

        retval += "\"Manifest\" : " + tuple.x.toString() + ",\n";

        retval += "\"DDS\" : [";
        for(Node node:tuple.y) {
            retval += "{";
            retval += "\"GUID\" : \"" + node.getNodeGUID().toString() + "\", ";
            retval += "\"Hostname\" : \"" + node.getHostAddress().getHostName() + "\", ";
            retval += "\"Port\" : " + node.getHostAddress().getPort();
            retval += "},";
        }
        if (tuple.y.size() > 0) {
            retval = retval.substring(0, retval.length() - 1); // removing last comma
        }
        retval += "]";

        retval += "}";

        return retval;
    }

}
