package uk.ac.standrews.cs.sos.network;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.RawBody;
import com.mashape.unirest.request.body.RequestBodyEntity;
import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;
import java.net.URL;
import java.security.PublicKey;

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

    public SyncRequest(PublicKey signatureCertificate, HTTPMethod method, URL url) {
        this(signatureCertificate, method, url, ResponseType.BINARY);
    }

    public SyncRequest(PublicKey signatureCertificate, HTTPMethod method, URL url, ResponseType responseType) {
        super(signatureCertificate, method, url);

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

    protected Response get() {

        GetRequest req = Unirest.get(url.toString());
        req = (GetRequest) setChallenge(req);

        return makeRequest(req);
    }

    private Response postJSON() {

        HttpRequestWithBody requestWithBody = Unirest.post(url.toString())
                .header("Content-Type", "application/json");
        requestWithBody = (HttpRequestWithBody) setChallenge(requestWithBody);

        RequestBodyEntity requestBodyEntity = requestWithBody.body(json_body);

        return makeRequest(requestBodyEntity);
    }

    private Response postData() throws IOException {

        // NOTE - this will most likely fail for large data
        byte[] bytes = IOUtils.toByteArray(inputStream);

        HttpRequestWithBody httpRequestWithBody = Unirest.post(url.toString());
        httpRequestWithBody = (HttpRequestWithBody) setChallenge(httpRequestWithBody);
        RawBody requestWithRawBody = httpRequestWithBody.body(bytes);

        return makeRequest(requestWithRawBody);
    }

    private Response putJSON() {

        HttpRequestWithBody requestWithBody = Unirest.put(url.toString())
                .header("accept", "application/json");
        requestWithBody = (HttpRequestWithBody) setChallenge(requestWithBody);

        RequestBodyEntity requestBodyEntity = requestWithBody.body(json_body);

        return makeRequest(requestBodyEntity);
    }

    private Response makeRequest(BaseRequest request) {
        try {
            HttpResponse<?> resp = null;
            switch(responseType) {
                case JSON:
                    resp = request.asString();
                    break;
                case TEXT:
                    resp = request.asString();
                    break;
                case BINARY:
                    resp = request.asBinary();
                    break;
            }

            if (signatureCertificate != null) {
                String signedChallenge = resp.getHeaders().getFirst(SOS_NODE_CHALLENGE_HEADER);
                boolean verified = DigitalSignature.verify64(signatureCertificate, nodeChallenge, signedChallenge);

                if (!verified) return new ErrorResponseImpl();
            }

            return new ResponseImpl(resp);
        } catch (UnirestException | CryptoException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to make HTTP request: " + e.getMessage());
            return new ErrorResponseImpl();
        } catch (Error e) {
            SOS_LOG.log(LEVEL.ERROR, "SyncRequest - Serious error: " + e.getMessage());
            return new ErrorResponseImpl();
        }

    }

    private HttpRequest setChallenge(HttpRequest httpRequest) {

        if (signatureCertificate != null) {
            httpRequest = httpRequest.header(SOS_NODE_CHALLENGE_HEADER, nodeChallenge);
        }

        return httpRequest;
    }

}
