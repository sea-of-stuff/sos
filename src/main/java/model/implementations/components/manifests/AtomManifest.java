package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.implementations.utils.GUIDsha1;
import model.implementations.utils.Location;
import model.interfaces.components.entities.Atom;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.Signature;
import org.apache.xmlbeans.impl.common.ReaderInputStream;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

/**
 * Manifest describing an Atom.
 *
 * <p>
 * Manifest - GUID <br>
 * ManifestType - ATOM <br>
 * Timestamp - ? <br>
 * Signature - signature of the manifest <br>
 * Locations - list of locations <br>
 * Content - GUID Content
 * </p>
 *
 * @see Atom
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifest extends BasicManifest {

    private GUID contentGUID;
    private Collection<Location> locations;

    /**
     * Creates a partially valid atom manifest given an atom.
     *
     * @param atom
     */
    protected AtomManifest(Atom atom) {
        super(ManifestConstants.ATOM);
        try {
            contentGUID = new GUIDsha1(atom.getSource().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        locations = atom.getSource().getLocations();
    }

    public GUID getGUIDContent() {
        return contentGUID;
    }

    public Collection<Location> getLocations() {
        return locations;
    }

    @Override
    public boolean verify() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isValid() {
        throw new NotImplementedException();
    }

    @Override
    public String toJSON() {
        JSONObject obj = new JSONObject();
        obj.put(ManifestConstants.KEY_TYPE, getManifestType());
        obj.put(ManifestConstants.KEY_LOCATIONS, getLocations());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, getGUIDContent());

        return obj.toString();
    }

    @Override
    protected GUID generateGUID() {

        GUID guid = null;
        try {
            String manifestStringRepresentation = generateManifestToHash();
            StringReader reader = new StringReader(manifestStringRepresentation);
            InputStream inputStream = new ReaderInputStream(reader, "UTF-8");
            guid = new GUIDsha1(inputStream);

            inputStream.close();
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return guid;
    }

    @Override
    protected Signature generateSignature(Identity identity) {
        return null;
    }

    /**
     * Generates a JSON representation of the part of the manifest that are used
     * to generate the GUID of this manifest.
     *
     * @return
     */
    private String generateManifestToHash() {
        JSONObject obj = new JSONObject();
        obj.put(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.put(ManifestConstants.KEY_LOCATIONS, locations);
        obj.put(ManifestConstants.KEY_CONTENT_GUID, contentGUID);

        return obj.toString();
    }

}
