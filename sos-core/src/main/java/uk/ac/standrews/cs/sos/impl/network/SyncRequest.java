package uk.ac.standrews.cs.sos.impl.network;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.body.RawBody;
import com.mashape.unirest.request.body.RequestBodyEntity;
import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SyncRequest extends Request {


    private ResponseType responseType;

    public SyncRequest(HTTPMethod method, URL url) {
        this(method, url, ResponseType.BINARY);
    }

    public SyncRequest(HTTPMethod method, URL url, ResponseType responseType) {
        super(method, url);

        this.responseType = responseType;
    }

    public Response play() throws IOException {
        SOS_LOG.log(LEVEL.INFO, "Play request. Method: " + method + " URL: " + url.toString());


        switch(method) {
            case GET: return get();
            case POST: return managePOST();
            case PUT: return managePUT();
            default:
                SOS_LOG.log(LEVEL.ERROR, "Unknown Request method while playing a request");
                throw new IOException("Unknown Request method");
        }

    }

    private Response managePOST() throws IOException {

        Response response;
        if (inputStream != null) {
            response = postData();
        } else if (json_body != null) {
            response = postJSON();
        } else {
            throw new IOException("No body to post");
        }

        return response;
    }

    private Response managePUT() throws IOException {

        Response response;
        if (json_body != null) {
            response = putJSON();
        } else {
            throw new IOException("No body to post");
        }

        return response;
    }

    protected Response get() throws IOException {

        GetRequest req = Unirest.get(url.toString());

        return makeRequest(req);
    }

    private Response postJSON() throws IOException {

        RequestBodyEntity requestWithBody = Unirest.post(url.toString())
                .header("Content-Type", "application/json")
                .body(json_body);

        return makeRequest(requestWithBody);
    }

    private Response postData() throws IOException {

        // FIXME - this will most likely fail for large data
        byte[] bytes = IOUtils.toByteArray(inputStream);

        RawBody requestWithRawBody = Unirest.post(url.toString())
                .body(bytes);

        return makeRequest(requestWithRawBody);
    }

    private Response putJSON() throws IOException {

        RequestBodyEntity requestWithBody = Unirest.put(url.toString())
                .header("accept", "application/json")
                .body(json_body);

        return makeRequest(requestWithBody);
    }

    private Response makeRequest(BaseRequest request) throws IOException {
        try {
            HttpResponse<?> resp = null;
            switch(responseType) {
                case JSON:
                    resp = request.asJson();
                    break;
                case TEXT:
                    resp = request.asString();
                    break;
                case BINARY:
                    resp = request.asBinary();
                    break;
            }

            return new ResponseImpl(resp);
        } catch (UnirestException e) {
            return new ErrorResponseImpl();
        }

    }

}
