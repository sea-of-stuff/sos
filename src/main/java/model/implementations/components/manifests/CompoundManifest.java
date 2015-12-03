package model.implementations.components.manifests;

import model.implementations.utils.Content;
import model.implementations.utils.GUID;
import org.json.JSONArray;
import org.json.JSONObject;

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
 * Contents - contents of this compound
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifest extends SignedManifest {

    private Collection<Content> contents;

    protected CompoundManifest(Collection<Content> contents) {
        super(ManifestConstants.COMPOUND);

        this.contents = contents;

        make();
    }

    private void make() {
        // TODO
    }

    public Collection<Content> getContents() {
        return contents;
    }

    @Override
    public boolean verify() {
        return false;
    }

    @Override
    public boolean isValid() {
        // TODO - test for signature?
        return super.isValid() && !contents.isEmpty();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = super.toJSON();

        obj.put(ManifestConstants.KEY_SIGNATURE, getSignature());
        obj.put(ManifestConstants.KEY_CONTENTS, getContentsInJSON());

        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    private JSONArray getContentsInJSON() {
        JSONArray arr = new JSONArray();
        for (Content content : contents) {
            arr.put(content.toJSON());
        }

        return arr;
    }

    @Override
    protected GUID generateGUID() {
        return null;
    }


}
