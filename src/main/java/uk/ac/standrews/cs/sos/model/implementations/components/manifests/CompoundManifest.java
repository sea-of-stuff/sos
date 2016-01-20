package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.configurations.identity.IdentityConfiguration;
import uk.ac.standrews.cs.sos.exceptions.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

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
public class CompoundManifest extends SignedManifest {

    private Collection<Content> contents;

    /**
     * Creates a valid compound manifest given a collection of contents and an
     * identity to sign the manifest.
     *
     * @param contents
     * @param identity
     * @throws ManifestNotMadeException
     */
    public CompoundManifest(Collection<Content> contents, Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.COMPOUND);
        this.contents = contents;

        makeContentGUID();

        if (identity != null)
            makeSignature();
    }

    /**
     * Gets all the contents of this compound.
     *
     * @return the contents of this compound.
     */
    public Collection<Content> getContents() {
        return contents;
    }

    @Override
    public boolean verify() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                contents != null &&
                !contents.isEmpty() &&
                isGUIDValid(contentGUID);
    }

    @Override
    public JsonObject toJSON() {
        JsonObject obj = super.toJSON();

        obj.addProperty(ManifestConstants.KEY_SIGNATURE, getSignature());
        obj.addProperty(ManifestConstants.KEY_CONTENT_GUID, contentGUID.toString());
        obj.add(ManifestConstants.KEY_CONTENTS, getContentsInJSON());

        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    @Override
    protected String generateSignature() throws EncryptionException {
        JsonObject obj = new JsonObject();

        obj.addProperty(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.addProperty(ManifestConstants.KEY_CONTENT_GUID, contentGUID.toString());

        Gson gson = new Gson();
        byte[] signatureBytes = this.identity.encrypt(gson.toJson(obj));
        return IdentityConfiguration.bytesToHex(signatureBytes);
    }

    public void makeContentGUID() throws ManifestNotMadeException {
        try {
            contentGUID = generateContentGUID();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }
    }

    private void makeSignature() throws ManifestNotMadeException  {
        try {
            signature = generateSignature();
        } catch (Exception e) {
            throw new ManifestNotMadeException();
        }
    }

    private GUID generateContentGUID() throws GuidGenerationException {
         return generateGUID(getContentsInJSON().toString());
    }

    private JsonArray getContentsInJSON() {
        JsonArray arr = new JsonArray();
        for (Content content : contents) {
            arr.add(content.toJSON());
        }

        return arr;
    }

}
