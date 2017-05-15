package uk.ac.standrews.cs.sos.impl.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.actors.SOSRMS;
import uk.ac.standrews.cs.sos.json.CompoundManifestDeserializer;
import uk.ac.standrews.cs.sos.json.CompoundManifestSerializer;
import uk.ac.standrews.cs.sos.model.*;

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

    final private Set<Content> contents;
    final private CompoundType type;

    /**
     * Creates a valid compound manifest given a collection of contents and an
     * identity to sign the manifest.
     *
     * @param contents
     * @param signer
     * @throws ManifestNotMadeException
     */
    public CompoundManifest(CompoundType type, Set<Content> contents, IGUID signer)
            throws ManifestNotMadeException {
        super(signer, ManifestType.COMPOUND);

        this.type = type;
        this.contents = contents;
        this.contentGUID = makeContentGUID();

        if (signer != null) {
            this.signature = makeSignature();
        }
    }

    public CompoundManifest(CompoundType type, IGUID contentGUID, Set<Content> contents, IGUID signer, String signature) throws ManifestNotMadeException {
        super(signer, ManifestType.COMPOUND);

        assert(type != null);

        this.type = type;
        this.contentGUID = contentGUID;
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
                isGUIDValid(contentGUID);
    }

    @Override
    public IGUID guid() {
        return contentGUID;
    }

    @Override
    protected String generateSignature(String toSign) throws EncryptionException {
        Role role = SOSRMS.instance().getRole(signer);

        if (role == null) {
            return "";
        } else {
            return role.sign(toSign);
        }
    }

    private IGUID makeContentGUID() throws ManifestNotMadeException {
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
        String toSign = getType() +
                "T" + getCompoundType() +
                "C" + guid();

        return toSign;
    }

    private IGUID generateContentGUID() throws GUIDGenerationException {
        StringBuilder toHash = new StringBuilder("C");
        for(Content content:contents) {
            toHash.append(content.toString());
        }

        return GUIDFactory.generateGUID(toHash.toString());
    }

}