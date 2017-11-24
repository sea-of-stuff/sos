package uk.ac.standrews.cs.sos.impl.manifest;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.IKey;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

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

    private int size = -1;

    protected IGUID guid;
    protected ManifestType manifestType;

    /**
     * Constructor for a BasicManifest.
     * Initialise the type of manifest.
     *
     * @param manifestType
     */
    protected BasicManifest(ManifestType manifestType) {
        this.manifestType = manifestType;
    }

    /**
     * Gets the type of this manifest.
     *
     * @return the type of this manifest.
     */
    @Override
    public ManifestType getType() {
        return this.manifestType;
    }

    @Override
    public abstract InputStream contentToHash() throws IOException;

    /**
     * Checks whether this manifest contains valid key-value entries.
     *
     * @return true if the manifest is valid.
     */
    @Override
    public boolean isValid() {
        return hasManifestType();
    }

    @Override
    public int size() {

        if (size == -1) {
            size = this.toString().length();
        }

        return size;
    }

    /**
     * Checks if the given GUID contains valid hex characters.
     *
     * @param guid to validated.
     * @return true if the guid is valid.
     */
    protected boolean isGUIDValid(IGUID guid) {
        if (guid == null || guid.isInvalid())
            return false;

        return true;
    }

    private boolean hasManifestType() {
        return manifestType != null;
    }

    /**
     * Transform this object into a JSON string
     *
     * @return
     */
    @Override
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to generate JSON for manifest object " + guid());
            return "";
        }

    }

    protected String getCollectionToHashOrSign(Set<IGUID> collection) {

        return collection.stream()
                .sorted(Comparator.comparing(IGUID::toMultiHash))
                .map(IKey::toMultiHash)
                .collect(Collectors.joining("."));
    }

    protected IGUID makeGUID() {

        try (InputStream content = contentToHash()){
            return GUIDFactory.generateGUID(GUID_ALGORITHM, content);
        } catch (GUIDGenerationException | IOException e) {
            return new InvalidID();
        }
    }

}
