package uk.ac.standrews.cs.sos.impl.metadata;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.json.MetadataSerializer;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Role;

import java.util.HashMap;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = MetadataSerializer.class)
public class MetadataManifest extends AbstractMetadata implements Metadata {

    MetadataManifest(ManifestType manifestType, Role signer) {
        super(manifestType, signer);

        metadata = new HashMap<>();
    }

    public MetadataManifest(HashMap<String, MetaProperty> metadata, Role signer) throws ManifestNotMadeException {
        this(ManifestType.METADATA, signer);

        this.metadata = metadata;
        this.guid = makeGUID();

        if (guid.isInvalid()) {
            throw new ManifestNotMadeException("Unable to make proper metadata manifest");
        }

        try {
            this.signature = makeSignature();
        } catch (SignatureException e) {
            throw new ManifestNotMadeException("Unable to sign the manifest");
        }

    }

    public MetadataManifest(IGUID guid, HashMap<String, MetaProperty> metadata, Role signer, String signature) {
        this(ManifestType.METADATA, signer);

        this.guid = guid;
        this.metadata = metadata;
        this.signature = signature;
    }

}
