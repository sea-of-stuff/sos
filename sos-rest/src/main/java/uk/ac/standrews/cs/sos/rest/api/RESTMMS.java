package uk.ac.standrews.cs.sos.rest.api;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.impl.metadata.basic.BasicMetadata;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.MMSNode;
import uk.ac.standrews.cs.sos.services.MetadataService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/sos/mms/")
@MMSNode
public class RESTMMS {

    @POST
    @Path("/metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response postMetadata(String json) throws IOException {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /dds/metadata");

        Metadata metadata = JSONHelper.JsonObjMapper().readValue(json, BasicMetadata.class);

        MetadataService metadataService = RESTConfig.sos.getMMS();
        try {
            metadataService.addMetadata(metadata);
            return HTTPResponses.CREATED(metadata.toString());

        } catch (MetadataPersistException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }


    }

    @GET
    @Path("/metadata/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getMetadata(@PathParam("guid") String guid) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /dds/metadata/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID metadataGUID;
        try {
            metadataGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        MetadataService metadataService = RESTConfig.sos.getMMS();
        try {
            Metadata metadata = metadataService.getMetadata(metadataGUID);
            return HTTPResponses.OK(metadata.toString());
        } catch (MetadataNotFoundException e) {
            return HTTPResponses.BAD_REQUEST("Invalid Input");
        }
    }

    @POST
    @Path("/process")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processMetadata(final InputStream inputStream) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /process");

        MetadataService MetadataService = RESTConfig.sos.getMMS();

        try {
            Data data = new InputStreamData(inputStream);
            Metadata metadata = MetadataService.processMetadata(data);

            return HTTPResponses.OK(metadata.toString());
        } catch (MetadataException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }


    }

}
