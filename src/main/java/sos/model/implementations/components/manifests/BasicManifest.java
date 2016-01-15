package sos.model.implementations.components.manifests;

import com.google.gson.JsonObject;
import org.apache.xmlbeans.impl.common.ReaderInputStream;
import sos.exceptions.GuidGenerationException;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.interfaces.components.Manifest;

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

    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");

    protected final String manifestType;

    /**
     * Constructor for a BasicManifest.
     * Initialise the type of manifest.
     *
     * @param manifestType
     */
    protected BasicManifest(String manifestType) {
        this.manifestType = manifestType;
    }

    /**
     * Verifies this manifest's GUID against its content.
     *
     * @return true if the GUID of the manifest matches the content.
     * @throws GuidGenerationException if the manifest's GUID could not be generated.
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
        return isManifestTypeValid();
    }

    /**
     * Transforms the content of this manifest to a JSON representation.
     *
     * @return JSON representation of this manifest.
     */
    @Override
    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty(ManifestConstants.KEY_TYPE, manifestType);

        return obj;
    }

    /**
     * Gets the type of this manifest.
     *
     * @return the type of this manifest.
     */
    @Override
    public String getManifestType() {
        return this.manifestType;
    }

    /**
     * Gets the GUID of the content referenced by this manifest.
     *
     * @return guid of the content.
     */
    public abstract GUID getContentGUID();

    /**
     * Checks if the given GUID contains valid hex characters.
     *
     * @param guid to validated.
     * @return true if the guid is valid.
     */
    protected boolean isGUIDValid(GUID guid) {
        Matcher matcher = HEX_PATTERN.matcher(guid.toString());
        return matcher.matches();
    }

    private boolean isManifestTypeValid() {
        return manifestType != null && !manifestType.isEmpty();
    }

    /**
     * Generates a GUID given a string.
     *
     * @param string used to generate the GUID.
     * @return GUID of the string.
     * @throws GuidGenerationException if the GUID could not be generated.
     */
    protected GUID generateGUID(String string) throws GuidGenerationException {
        GUID guid;

        try (StringReader reader = new StringReader(string);
             InputStream inputStream = new ReaderInputStream(reader, "UTF-8")) {

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
     * Generates a GUID given an InputStream.
     *
     * @param inputStream used to generate the GUID.
     * @return GUID of the input stream.
     * @throws GuidGenerationException if the GUID could not be generated.
     */
    protected GUID generateGUID(InputStream inputStream) throws GuidGenerationException {
        return new GUIDsha1(inputStream);
    }

}
