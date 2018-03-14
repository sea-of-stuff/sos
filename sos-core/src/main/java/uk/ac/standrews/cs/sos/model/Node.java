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
package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.sos.impl.json.NodeDeserializer;
import uk.ac.standrews.cs.sos.impl.json.NodeSerializer;

import java.net.InetSocketAddress;
import java.security.PublicKey;

/**
 * Node interface
 *
 * GUID = hash(signature certificate)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = NodeSerializer.class)
@JsonDeserialize(using = NodeDeserializer.class)
public interface Node extends Manifest {

    /**
     * This is the signature certificate the the node can expose to the rest of the SOS.
     *
     * @return public certificate of node. This is used to verify the node requests.
     */
    PublicKey getSignatureCertificate();

    /**
     * This is the address of the node.
     * This information should be used to contact the node.
     *
     * @return address of node
     */
    InetSocketAddress getHostAddress();

    /**
     * IP address as a string
     * @return IP
     */
    String getIP();

    /**
     * Returns true if this is a client node
     * @return true if the node is an agent
     */
    boolean isAgent();

    /**
     * Returns true if this is a storage node
     * @return true if the node exposes the storage service
     */
    boolean isStorage();

    /**
     * Returns true if this is a MDS node
     * @return true if the MDS service is exposed
     */
    boolean isMDS();

    /**
     * Returns true if this is a NDS node
     * @return true if the NDS service is exposed
     */
    boolean isNDS();

    /**
     * Returns true if this is a MMS node
     * @return true if the MMS service is exposed
     */
    boolean isMMS();

    /**
     * Returns true if this is a CMS node
     *
     * @return true if the CMS service is exposed
     */
    boolean isCMS();

    /**
     * Returns true if this is a RMS node
     * @return true if the RMS service is exposed
     */
    boolean isRMS();

    boolean isExperiment();

}
