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
package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.PublicKey;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicNode extends BasicManifest implements Node {

    private String ip;
    private InetSocketAddress hostname;

    public BasicNode(String ip, int port) {
        super(ManifestType.NODE);

        guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        this.ip = ip;
        this.hostname = new InetSocketAddress(ip, port);
    }

    @Override
    public InputStream contentToHash() {
        return new StringData("").getInputStream();
    }

    @Override
    public PublicKey getSignatureCertificate() {
        return null;
    }

    @Override
    public InetSocketAddress getHostAddress() {
        return hostname;
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public boolean isStorage() {
        return true;
    }

    @Override
    public boolean isMDS() {
        return true;
    }

    @Override
    public boolean isNDS() {
        return false;
    }

    @Override
    public boolean isMMS() {
        return false;
    }

    @Override
    public boolean isCMS() {
        return true;
    }

    @Override
    public boolean isRMS() {
        return false;
    }

    @Override
    public boolean isExperiment() {
        return true;
    }
}
