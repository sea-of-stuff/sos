package model.implementations.components.manifests;

import model.exceptions.GuidGenerationException;
import model.implementations.utils.GUID;
import model.implementations.utils.GUIDsha1;
import model.implementations.utils.Location;
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
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifest extends BasicManifest {

    private GUID contentGUID;
    private Collection<Location> locations;

    /**
     * Creates a partially valid atom manifest given an atom.
     *
     * @param locations
     */
    protected AtomManifest(Collection<Location> locations) {
        super(ManifestConstants.ATOM);

        this.locations = locations;

        // TODO - generate guid
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
    protected GUID generateGUID() throws GuidGenerationException {

        GUID guid = null;
        String manifestStringRepresentation = generateManifestToHash();
        try (StringReader reader = new StringReader(manifestStringRepresentation);
             InputStream inputStream = new ReaderInputStream(reader, "UTF-8");) {

            guid = new GUIDsha1(inputStream);
        } catch (UnsupportedEncodingException e) {
            throw new GuidGenerationException("UnsupportedEncoding");
        } catch (IOException e) {
            throw new GuidGenerationException("IO exception");
        }
        return guid;
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
        obj.put(ManifestConstants.KEY_CONTENT_GUID, contentGUID);

        return obj.toString();
    }

}
