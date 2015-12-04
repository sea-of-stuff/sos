package model.implementations.components.manifests;

import model.exceptions.GuidGenerationException;
import model.implementations.utils.GUID;
import model.implementations.utils.GUIDsha1;
import model.interfaces.SeaOfStuff;
import model.interfaces.components.Manifest;
import org.apache.xmlbeans.impl.common.ReaderInputStream;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The BasicManifest defines the base implementation for all other manifests.
 * This class implements some of the methods that can be generalised across all
 * other types of manifests. Manifests extending the BasicManifest MUST provide
 * implementations for the abstract methods defined in this class.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BasicManifest implements Manifest {

    Pattern hexPattern = Pattern.compile("^[0-9a-fA-F]+$");

    private GUID manifestGuid;
    private final String manifestType;

    /**
     * Constructor for a BasicManifest.
     * Initialise the type of manifest and the timestamp for this manifest.
     *
     * @param manifestType
     */
    protected BasicManifest(String manifestType) {
        this.manifestType = manifestType;
    }

    /**
     * Verify this manifest's GUID against its content.
     *
     * {@link SeaOfStuff#verifyManifest(Manifest)}
     *
     * @return true if the GUID of the manifest matches the content.
     */
    @Override
    public abstract boolean verify() throws GuidGenerationException;

    /**
     * Checks whether this manifest contains valid key-value entries.
     *
     * @return true if the manifest is valid.
     */
    @Override
    public boolean isValid() {
        return isGUIDValid(manifestGuid) && isManifestTypeValid();
    }

    /**
     * Transform the content of this manifest to a JSON representation.
     *
     * @return JSON representation of this manifest.
     */
    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        obj.put(ManifestConstants.KEY_MANIFEST_GUID, manifestGuid);
        obj.put(ManifestConstants.KEY_TYPE, manifestType);

        return obj;
    }

    @Override
    public GUID getManifestGUID() {
        return this.manifestGuid;
    }

    @Override
    public String getManifestType() {
        return this.manifestType;
    }

    /**
     *
     * @param guid
     * @return
     */
    protected boolean isGUIDValid(GUID guid) {
        Matcher matcher = hexPattern.matcher(guid.toString());
        return matcher.matches();
    }

    private boolean isManifestTypeValid() {
        return manifestType != null && !manifestType.isEmpty();
    }

    /**
     * Generates and set the manifest guid.
     * Assuming that the function generateManifestToHash has been implemented.
     *
     * @throws GuidGenerationException
     */
    protected void generateManifestGUID() throws GuidGenerationException {
        String manifest = generateManifestToHash();
        manifestGuid = generateGUID(manifest);
    }

    /**
     *
     * @param string
     * @return
     * @throws GuidGenerationException
     */
    protected GUID generateGUID(String string) throws GuidGenerationException {
        GUID guid = null;

        try (StringReader reader = new StringReader(string);
             InputStream inputStream = new ReaderInputStream(reader, "UTF-8");) {

            guid = generateGUID(inputStream);
        } catch (UnsupportedEncodingException e) {
            throw new GuidGenerationException("Unsupported Encoding");
        } catch (IOException e) {
            throw new GuidGenerationException("IO Exception");
        } catch (Exception e) {
            throw new GuidGenerationException("General Exception");
        }
        return guid;
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws GuidGenerationException
     */
    protected GUID generateGUID(InputStream inputStream) throws GuidGenerationException {
        return new GUIDsha1(inputStream);
    }

    /**
     * Generates a JSON representation of the part of the manifest that are used
     * to generate the GUID of this manifest.
     *
     * @return
     */
    protected abstract String generateManifestToHash();

}
