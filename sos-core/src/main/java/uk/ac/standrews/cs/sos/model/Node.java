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
     * Returns true if this is a DDS node
     * @return true if the DDS service is exposed
     */
    boolean isDDS();

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
