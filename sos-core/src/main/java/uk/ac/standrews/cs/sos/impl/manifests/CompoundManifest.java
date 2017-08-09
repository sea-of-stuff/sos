package uk.ac.standrews.cs.sos.impl.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.json.CompoundManifestDeserializer;
import uk.ac.standrews.cs.sos.json.CompoundManifestSerializer;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * A compound is an immutable collection of (references to)
 * atoms or other compounds (contents).
 * Compounds do not contain data - they refer to data - and are identified by
 * GUID (derived from their contents).
 *
 * <p>
 * Intuition: <br>
 * Compounds are provided to permit related atoms and compounds to be gathered
 * together (think of folders, zip files, packages etc. without containment).
 * <p>
 * A compound can be used for de-duplication. Two collections of data
 * (atoms and compounds) might contain the same content. The data does not have
 * to be duplicated for each compound, since we can uniquely refer to the data
 * from the compound itself.
 *
 * <br>
 * Manifest describing a Compound.
 * <p>
 * Manifest - GUID <br>
 * ManifestType - COMPOUND <br>
 * Signature - signature of the manifest <br>
 * ContentGUID - guid of the compound content <br>
 * Contents - contents of this compound
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = CompoundManifestSerializer.class)
@JsonDeserialize(using = CompoundManifestDeserializer.class)
public class CompoundManifest extends SignedManifest implements Compound {

    protected Set<Content> contents;
    protected CompoundType type;

    // Needed for SecureCompoundManifest (a bit of a hack)
    protected CompoundManifest(CompoundType compoundType, Role signer, ManifestType type) {
        super(signer, type);

        this.type = compoundType;
    }

    /**
     * Creates a valid compound manifest given a collection of contents and an
     * identity to sign the manifest.
     *
     * @param contents
     * @param signer
     * @throws ManifestNotMadeException
     */
    public CompoundManifest(CompoundType type, Set<Content> contents, Role signer) throws ManifestNotMadeException {
        super(signer, ManifestType.COMPOUND);

        this.type = type;
        this.contents = contents;
        this.guid = makeContentGUID();

        if (signer != null) {
            try {
                this.signature = makeSignature();
            } catch (SignatureException e) {
                // We keep the signature NULL
            }
        }
    }

    public CompoundManifest(CompoundType type, IGUID contentGUID, Set<Content> contents, Role signer, String signature) throws ManifestNotMadeException {
        super(signer, ManifestType.COMPOUND);

        assert(type != null);

        this.type = type;
        this.guid = contentGUID;
        this.contents = contents;
        this.signature = signature;
    }

    /**
     * Gets all the contents of this compound.
     *
     * @return the contents of this compound.
     */
    @Override
    public Set<Content> getContents() {
        return contents;
    }

    @Override
    public CompoundType getCompoundType() {
        return type;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                type != null &&
                contents != null &&
                isGUIDValid(guid);
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public InputStream contentToHash() {

        StringBuilder toHash = new StringBuilder("C");
        for(Content content:contents) {
            toHash.append(content.toString());
        }

        return IO.StringToInputStream(toHash.toString());
    }

    @Override
    protected String generateSignature(String toSign) throws SignatureException {

        if (signer == null) {
            return "";
        } else {
            return signer.sign(toSign);
        }
    }

    protected IGUID makeContentGUID() throws ManifestNotMadeException {
        IGUID guid;
        try {
            guid = generateContentGUID();
        } catch (GUIDGenerationException e) {
            throw new ManifestNotMadeException("Failed to generate content GUID");
        }
        return guid;
    }

    @Override
    protected String getManifestToSign() {

        return getType() +
                "T" + getCompoundType() +
                "C" + guid();
    }

    private IGUID generateContentGUID() throws GUIDGenerationException {

        try (InputStream inputStream = contentToHash()) {

            return GUIDFactory.generateGUID(ALGORITHM.SHA256, inputStream);

        } catch (IOException e) {
            throw new GUIDGenerationException();
        }
    }

}
