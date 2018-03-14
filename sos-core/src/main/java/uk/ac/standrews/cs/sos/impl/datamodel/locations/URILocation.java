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
package uk.ac.standrews.cs.sos.impl.datamodel.locations;

import org.apache.commons.lang3.StringUtils;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.network.HTTPMethod;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.ResponseType;
import uk.ac.standrews.cs.sos.network.SyncRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static uk.ac.standrews.cs.sos.constants.LocationSchemes.*;

/**
 * supported schemes: http, https, file, ftp, etc
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URILocation implements Location {

    private URI uri;

    public URILocation(String location) throws URISyntaxException {
        String scheme = getScheme(location);
        String host = getHost(scheme, location);
        String path = getPath(scheme, location);

        uri = new URI(scheme, host, path, null);
    }

    private String getScheme(String location) {
        if (location.startsWith("/") || location.startsWith("file://"))
            return FILE_SCHEME;

        if (location.startsWith("https://"))
            return HTTPS_SCHEME;

        if (location.startsWith("http://"))
            return HTTP_SCHEME;

        return "";
    }

    private String getHost(String scheme, String location) {

        switch(scheme) {
            case FILE_SCHEME:
                return "localhost";
            default:
                return location.split("/")[2];
        }
    }

    private String getPath(String scheme, String location) {

        switch(scheme) {
            case FILE_SCHEME:
                if (location.startsWith("/"))
                    return location;
                else {
                    int indexOfThirdSlash = StringUtils.ordinalIndexOf(location, "/", 3);
                    return location.substring(indexOfThirdSlash);
                }
            default:
                int indexOfThirdSlash = StringUtils.ordinalIndexOf(location, "/", 3);
                return location.substring(indexOfThirdSlash);
        }
    }

    @Override
    public URI getURI() {
        return uri;
    }

    public InputStream getSource() throws IOException {
        String scheme = uri.getScheme();

        switch(scheme) {
            case FILE_SCHEME:
                return getFileSource();
            case HTTP_SCHEME:
                return getHTTPSource();
            case HTTPS_SCHEME:
                return getHTTPSSource();
            default:
                throw new IOException("Scheme " + scheme + " not supported");
        }

    }

    private InputStream getHTTPSource() throws IOException {

        SyncRequest request = new SyncRequest(HTTPMethod.GET, uri.toURL(), ResponseType.BINARY);
        Response response = RequestsManager.getInstance().playSyncRequest(request);

        return response.getBody();
    }

    private InputStream getHTTPSSource() throws IOException {

        return getHTTPSource();
    }

    private InputStream getFileSource() throws IOException {
        return uri.toURL().openStream();
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URILocation that = (URILocation) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
