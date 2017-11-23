package uk.ac.standrews.cs.sos.impl.datamodel;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.manifest.AbstractSignedManifest;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.InputStream;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

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
public class CompoundManifest extends AbstractSignedManifest implements Compound {

    protected Set<Content> contents;
    protected CompoundType type;

    // Needed for SecureCompoundManifest (a bit of a hack)
    protected CompoundManifest(CompoundType compoundType, Role signer, ManifestType type) {
        super(signer, type);

        this.type = compoundType;
    }

    /**
     * Creates a valid compound manifest given a collection of contents and an
     * identity to sign the manifest.
     *
     * @param contents
     * @param signer
     * @throws ManifestNotMadeException
     */
    public CompoundManifest(CompoundType type, Set<Content> contents, Role signer) throws ManifestNotMadeException {
        this(type, signer, ManifestType.COMPOUND);

        this.contents = contents;
        this.guid = makeGUID();

        if (guid.isInvalid()) {
            throw new ManifestNotMadeException("Failed to generate content GUID");
        }

        if (signer != null) {
            try {
                this.signature = makeSignature();
            } catch (SignatureException e) {
                throw new ManifestNotMadeException("Unable to generate signature for manifest");
            }
        }
    }

    public CompoundManifest(CompoundType type, IGUID contentGUID, Set<Content> contents, Role signer, String signature) {
        super(signer, ManifestType.COMPOUND);

        assert(type != null);

        this.type = type;
        this.guid = contentGUID;
        this.contents = contents;
        this.signature = signature;
    }

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
                isGUIDValid(guid);
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public InputStream contentToHash() {

        String toHash = getType() +
                "T" + getCompoundType() +
                "C" + contents.stream()
                    .sorted(Comparator.comparing(Content::toString))
                    .map(Object::toString)
                    .collect(Collectors.joining("."));

        return IO.StringToInputStream(toHash);
    }

    @Override
    protected String generateSignature(String toSign) throws SignatureException {

        if (signer == null) {
            return "";
        } else {
            return signer.sign(toSign);
        }
    }

}
