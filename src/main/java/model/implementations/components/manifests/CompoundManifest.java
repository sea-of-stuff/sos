package model.implementations.components.manifests;

import configurations.identity.IdentityConfiguration;
import model.exceptions.EncryptionException;
import model.exceptions.GuidGenerationException;
import model.exceptions.ManifestNotMadeException;
import model.implementations.utils.Content;
import model.implementations.utils.GUID;
import model.interfaces.identity.Identity;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    private GUID contentGUID;
    private Collection<Content> contents;

    /**
     * Creates a valid compound manifest given a collection of contents and an
     * identity to sign the manifest.
     *
     * @param contents
     * @param identity
     * @throws ManifestNotMadeException
     */
    protected CompoundManifest(Collection<Content> contents, Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.COMPOUND);
        this.contents = contents;

        make();
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
        // TODO - verify the GUID of the content against the actual content.
        throw new NotImplementedException();
    }

    @Override
    public boolean isValid() {
        // TODO - test for signature?
        return super.isValid() &&
                !contents.isEmpty() &&
                isGUIDValid(contentGUID);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = super.toJSON();

        obj.put(ManifestConstants.KEY_SIGNATURE, getSignature());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, contentGUID);
        obj.put(ManifestConstants.KEY_CONTENTS, getContentsInJSON());

        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    @Override
    protected JSONObject generateManifestToHash() {
        JSONObject obj = new JSONObject();

        obj.put(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.put(ManifestConstants.KEY_SIGNATURE, getSignature());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, contentGUID);

        return obj;
    }

    @Override
    protected void generateSignature() throws EncryptionException {
        JSONObject obj = new JSONObject();

        obj.put(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, contentGUID);

        byte[] signatureBytes = this.identity.encrypt(obj.toString());
        signature = IdentityConfiguration.bytesToHex(signatureBytes);
    }

    private void make() throws ManifestNotMadeException {

        try {
            generateContentGUID();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }

        try {
            generateSignature();
        } catch (Exception e) {
            throw new ManifestNotMadeException();
        }

        try {
            generateManifestGUID();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }
    }

    private void generateContentGUID() throws GuidGenerationException {
        contentGUID = generateGUID(getContentsInJSON().toString());
    }

    private JSONArray getContentsInJSON() {
        JSONArray arr = new JSONArray();
        for (Content content : contents) {
            arr.put(content.toJSON());
        }

        return arr;
    }

}
