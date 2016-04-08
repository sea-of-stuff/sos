package uk.ac.standrews.cs.sos.model.manifests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;

import java.util.Collection;

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
public class CompoundManifest extends SignedManifest implements Compound {

    final private Collection<Content> contents;
    final private CompoundType type;

    /**
     * Creates a valid compound manifest given a collection of contents and an
     * identity to sign the manifest.
     *
     * @param contents
     * @param identity
     * @throws ManifestNotMadeException
     */
    public CompoundManifest(CompoundType type, Collection<Content> contents, Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.COMPOUND);
        this.type = type;
        this.contents = contents;
        this.contentGUID = makeContentGUID();

        if (identity != null) {
            this.signature = makeSignature();
        }
    }

    /**
     *
     * @param contentGUID
     * @param contents
     * @param signature
     */
    public CompoundManifest(CompoundType type, IGUID contentGUID, Collection<Content> contents, String signature) {
        super(null, ManifestConstants.COMPOUND);
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
    public Collection<Content> getContents() {
        return contents;
    }

    @Override
    public CompoundType getType() {
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
    public JsonObject toJSON() {
        JsonObject obj = super.toJSON();

        obj.addProperty(ManifestConstants.KEY_COMPOUND_TYPE, type.toString());
        obj.addProperty(ManifestConstants.KEY_CONTENT_GUID, contentGUID.toString());
        obj.add(ManifestConstants.KEY_CONTENTS, getContentsInJSON());

        if (signature != null && !signature.isEmpty()) {
            obj.addProperty(ManifestConstants.KEY_SIGNATURE, signature);
        }

        return obj;
    }

    @Override
    protected String generateSignature(String toSign) throws EncryptionException {
        byte[] signatureBytes = this.identity.sign(toSign);
        byte[] encodedBytes = Base64.encodeBase64(signatureBytes);
        return new String(encodedBytes);
    }

    private IGUID makeContentGUID() throws ManifestNotMadeException {
        IGUID guid;
        try {
            guid = generateContentGUID();
        } catch (GUIDGenerationException e) {
            throw new ManifestNotMadeException();
        }
        return guid;
    }

    @Override
    protected String getManifestToSign() {
        JsonObject obj = new JsonObject();

        obj.addProperty(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.addProperty(ManifestConstants.KEY_COMPOUND_TYPE, type.toString());
        obj.addProperty(ManifestConstants.KEY_CONTENT_GUID, contentGUID.toString());

        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    private IGUID generateContentGUID() throws GUIDGenerationException {
         return GUIDFactory.generateGUID(getContentsInJSON().toString());
    }

    private JsonArray getContentsInJSON() {
        JsonArray arr = new JsonArray();
        for (Content content : contents) {
            arr.add(content.toJSON());
        }

        return arr;
    }

}
