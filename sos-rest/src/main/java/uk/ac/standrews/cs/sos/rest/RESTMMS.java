package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.bindings.MCSNode;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.MMS;
import uk.ac.standrews.cs.sos.interfaces.model.SOSMetadata;
import uk.ac.standrews.cs.sos.model.metadata.basic.BasicMetadata;
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
@Path("/mms/")
@MCSNode
public class RESTMMS {

    @POST
    @Path("/metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response postMetadata(String json) throws IOException {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /dds/metadata");

        SOSMetadata metadata = JSONHelper.JsonObjMapper().readValue(json, BasicMetadata.class);

        MMS mms = RESTConfig.sos.getMMS();
        try {
            mms.addMetadata(metadata);
        } catch (MetadataPersistException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        return HTTPResponses.CREATED(metadata.toString());
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

        MMS mms = RESTConfig.sos.getMMS();
        try {
            SOSMetadata metadata = mms.getMetadata(metadataGUID);
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

        MMS MMS = RESTConfig.sos.getMMS();

        SOSMetadata metadata;
        try {
            metadata = MMS.processMetadata(inputStream);
        } catch (MetadataException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }

        return HTTPResponses.OK(metadata.toString());
    }

}
