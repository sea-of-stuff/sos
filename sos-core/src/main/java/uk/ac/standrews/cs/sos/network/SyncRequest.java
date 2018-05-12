/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.network;


import com.mashape.unirest.http.HttpMethod;
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
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PublicKey;
import java.util.stream.Collectors;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SyncRequest extends Request {

    private ResponseType responseType;
    private String content_type;

    /**
     * Synchronous request
     * Response type is BINARY by default
     *
     * @param method of the request
     * @param url of the request
     */
    public SyncRequest(HTTPMethod method, URL url) {
        this(method, url, ResponseType.BINARY);
    }

    /**
     * Synchronous request
     *
     * @param method of the request
     * @param url of the request
     * @param responseType for the response
     */
    public SyncRequest(HTTPMethod method, URL url, ResponseType responseType) {
        super(method, url);

        this.responseType = responseType;
    }

    /**
     * Constructor for a signed synchronous request.
     * Response type is BINARY by default
     *
     * @param d_publicKey used to verify that the contacted node has signed the response properly
     * @param method of the request
     * @param url of the request
     */
    public SyncRequest(PublicKey d_publicKey, HTTPMethod method, URL url) {
        this(d_publicKey, method, url, ResponseType.BINARY);
    }

    /**
     * Constructor for a signed synchronous request.
     *
     * @param d_publicKey used to verify that the contacted node has signed the response properly
     * @param method of the request
     * @param url of the request
     * @param responseType for the response
     */
    public SyncRequest(PublicKey d_publicKey, HTTPMethod method, URL url, ResponseType responseType) {
        super(d_publicKey, method, url);

        this.responseType = responseType;
    }

    /**
     * Execute the request
     * @return the response to the request made
     * @throws IOException if the request could not be processed properly
     */
    Response play() throws IOException {
        SOS_LOG.log(LEVEL.INFO, "Play request. Method: " + method + " URL: " + url.toString());

        switch(method) {
            case GET: return get();
            case POST: return managePOST();
            case PUT: return managePUT();
            case DELETE: return delete();
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

    protected Response delete() {

        HttpRequestWithBody req = Unirest.delete(url.toString());
        req = (HttpRequestWithBody) setChallenge(req);

        return makeRequest(req);
    }

    private Response postJSON() {

        HttpRequestWithBody requestWithBody = Unirest.post(url.toString());
        requestWithBody = setContentType(requestWithBody,"application/json");
        requestWithBody = (HttpRequestWithBody) setChallenge(requestWithBody);

        RequestBodyEntity requestBodyEntity = requestWithBody.body(json_body);

        return makeRequest(requestBodyEntity);
    }

    private HttpRequestWithBody setContentType(HttpRequestWithBody req, String defaultContentType) {

        if (content_type != null && !content_type.isEmpty()) {
            req.header("Content-Type", content_type);
        } else {
            req.header("Content-Type", defaultContentType);
        }

        return req;
    }

    private Response postData() throws IOException {

        // NOTE - this will most likely fail for large data
        byte[] bytes = IOUtils.toByteArray(inputStream);

        HttpRequestWithBody httpRequestWithBody = Unirest.post(url.toString());
        httpRequestWithBody = setContentType(httpRequestWithBody,"multipart/form-data");

        httpRequestWithBody = (HttpRequestWithBody) setChallenge(httpRequestWithBody);
        RawBody requestWithRawBody = httpRequestWithBody.body(bytes);

        return makeRequest(requestWithRawBody);
    }

    private Response putJSON() {

        HttpRequestWithBody requestWithBody = Unirest.put(url.toString());
        requestWithBody = setContentType(requestWithBody,"application/json");
        requestWithBody = (HttpRequestWithBody) setChallenge(requestWithBody);

        RequestBodyEntity requestBodyEntity = requestWithBody.body(json_body);

        return makeRequest(requestBodyEntity);
    }

    private Response makeRequest(BaseRequest request) {

        try {
            signRequest(request);
            encryptRequest(request);

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

            if (d_publicKey != null) {
                String signedChallenge = resp.getHeaders().getFirst(SOS_NODE_CHALLENGE_HEADER);
                boolean verified = DigitalSignature.verify64(d_publicKey, nodeChallenge, signedChallenge);

                if (!verified) {
                    SOS_LOG.log(LEVEL.ERROR, "SyncRequest - Challenge not verified");
                    return new ErrorResponseImpl();
                }
            }

            return new ResponseImpl(resp);
        } catch (UnirestException | CryptoException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to make HTTP request: " + e.getMessage());
            return new ErrorResponseImpl();
        } catch (Error e) {
            SOS_LOG.log(LEVEL.ERROR, "SyncRequest - Serious error: " + e.getMessage());
            return new ErrorResponseImpl();
        }
    }

    // NOTE - The following method has not been tested
    // This method might be particularly slow for requests that have large bodies
    // I am not signing the body for the time being
    private void signRequest(BaseRequest request) throws CryptoException, IOException {

        if (d_privateKey != null) {
            HttpRequest httpRequest = request.getHttpRequest();
            String method = httpRequest.getHttpMethod().name();
            String url = httpRequest.getUrl();
            String headers = httpRequest.getHeaders()
                    .entrySet().stream()
                        .map(h -> h.getKey() + "=" + h.getValue()
                                                        .stream()
                                                        .collect(Collectors.joining())
                            )
                        .collect(Collectors.joining("&"));

            String body = "";
            if (httpRequest.getHttpMethod() == HttpMethod.POST || httpRequest.getHttpMethod() == HttpMethod.PUT) {
                try (InputStream bodyContent = httpRequest.getBody().getEntity().getContent()) {
                    body = IO.InputStreamToString(bodyContent);
                }
            }

            String requestToSign = method + url + headers + body + nodeChallenge;
            String signedChallenge = DigitalSignature.sign64(d_privateKey, requestToSign);

            request.getHttpRequest().header(SOS_NODE_SIGNED_CHALLENGE_HEADER, signedChallenge); // TODO - Have remote node verify the signature
        }

    }

    private void encryptRequest(BaseRequest request) {
        // TODO - This will be developed in the future to protect data in-transit
    }

    private HttpRequest setChallenge(HttpRequest httpRequest) {

        // If the public key is unknown, then there is no reason to challenge the contacted node
        if (d_publicKey != null) {
            httpRequest = httpRequest.header(SOS_NODE_CHALLENGE_HEADER, nodeChallenge);
        }

        return httpRequest;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
}
