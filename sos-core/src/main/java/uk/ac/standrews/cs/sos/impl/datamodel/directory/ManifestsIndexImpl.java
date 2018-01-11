package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsIndex;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Versionable;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO - set max size of indexes
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsIndexImpl implements ManifestsIndex, Serializable {

    // [type --> guid/invariant]
    // invariant for versionable manifests
    // guid for all others
    private transient HashMap<ManifestType, Set<IGUID>> typeToManifest;

    // [invariant --> [versionable]]
    private transient HashMap<IGUID, Set<IGUID>> assetsToVersions;

    // [invariant --> [versionable/tip]]
    private transient HashMap<IGUID, Set<IGUID>> tips;

    // [invariant --> versionable/head]
    private transient HashMap<IGUID, IGUID> heads;

    private static final long serialVersionUID = 1L;
    public ManifestsIndexImpl() {

        tips = new HashMap<>();
        heads = new HashMap<>();
        assetsToVersions = new HashMap<>();
        typeToManifest = new HashMap<>();
    }

    @Override
    public void track(Manifest manifest) {

        ManifestType type = manifest.getType();

        if (!typeToManifest.containsKey(type)) {
            typeToManifest.put(type, new LinkedHashSet<>());
        }

        if (manifest instanceof Versionable) {
            Versionable versionable = (Versionable) manifest;
            typeToManifest.get(type).add(versionable.invariant());
        } else {
            typeToManifest.get(type).add(manifest.guid());
        }

    }

    @Override
    public Set<IGUID> getManifests(ManifestType type) {

        if (typeToManifest.containsKey(type)) {
            return typeToManifest.get(type);
        } else {
            return new LinkedHashSet<>();
        }
    }

    @Override
    public Set<IGUID> getVersions(IGUID invariant) {

        if (assetsToVersions.containsKey(invariant)) {
            return assetsToVersions.get(invariant);
        } else {
            return new LinkedHashSet<>();
        }
    }

    @Override
    public Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException {

        if (tips.containsKey(invariant)) {
            return tips.get(invariant);
        }

        throw new TIPNotFoundException();
    }

    @Override
    public IGUID getHead(IGUID invariant) throws HEADNotFoundException {

        if (heads.containsKey(invariant)) {
            return heads.get(invariant);
        }

        throw new HEADNotFoundException();
    }

    @Override
    public void setHead(Versionable versionable) {

        IGUID invariant = versionable.invariant();
        IGUID version = versionable.guid();

        heads.put(invariant, version);
        addVersionInAsset(invariant, version);
    }

    @Override
    public void advanceTip(Versionable versionable) {

        Set<IGUID> previousVersions = versionable.previous();

        IGUID guid = versionable.guid();
        IGUID invariant = versionable.invariant();

        if (previousVersions == null || previousVersions.isEmpty()) {
            advanceTip(invariant, guid);
        } else {
            advanceTip(invariant, versionable.previous(), guid);
        }

        addVersionInAsset(invariant, guid);
    }

    @Override
    public void delete(Manifest manifest) {
        // TODO - ad-hoc tests

        ManifestType type = manifest.getType();
        IGUID guid = manifest.guid();

        // 1. Remove from typeToManifest
        if (typeToManifest.containsKey(type)) {
            typeToManifest.get(type).remove(guid);
        }

        if (manifest instanceof Versionable) {
            Versionable versionable = (Versionable) manifest;
            IGUID invariant = versionable.invariant();

            // 2. assetsToVersions
            if (assetsToVersions.containsKey(invariant)) {
                assetsToVersions.get(invariant).remove(guid);
            }

            Set<IGUID> previous = versionable.previous();

            // 3. tips (FIXME set prev is exists - NOT SO EASY FOR TIPS) - see page 88 of notebook
            if (tips.containsKey(invariant)) {
                tips.get(invariant).remove(guid);
            }

            // 4. heads. Set prev is exists, else delete entry
            if (heads.containsKey(invariant)) {
                if (previous != null && !previous.isEmpty()) {
                    IGUID firstPrevious = previous.iterator().next();
                    heads.put(invariant, firstPrevious);
                } else {
                    heads.remove(invariant);
                }
            }
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void rebuild() {

        // TODO - FORCE THE INDEX TO BE REBUILT FROM THE LOCAL DIRECTORY
    }

    @Override
    public void clear() {

        tips.clear();
        heads.clear();
        assetsToVersions.clear();
        typeToManifest.clear();
    }

    private void advanceTip(IGUID invariant, IGUID version) {

        if (!tips.containsKey(invariant)) {
            tips.put(invariant, new LinkedHashSet<>());
        }

        tips.get(invariant).add(version);
    }

    private void advanceTip(IGUID invariant, Set<IGUID> previousVersions, IGUID newVersion) {

        if (tips.containsKey(invariant) && tips.get(invariant).containsAll(previousVersions)) {

            advanceTip(invariant, newVersion);
            tips.get(invariant).removeAll(previousVersions); // Remove the previous tips, which are now replaced by the newVersion
        } else {
            // This is the case when we are adding a tip to a new branch.
            advanceTip(invariant, newVersion);
        }
    }

    private void addVersionInAsset(IGUID invariant, IGUID version) {
        // Update the [invariant --> [version]] map
        if (!assetsToVersions.containsKey(invariant)) {
            assetsToVersions.put(invariant, new LinkedHashSet<>());
        }
        assetsToVersions.get(invariant).add(version);
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(tips.size());
        for(Map.Entry<IGUID, Set<IGUID>> tip : tips.entrySet()) {
            out.writeUTF(tip.getKey().toMultiHash());
            out.writeInt(tip.getValue().size());

            for(IGUID t:tip.getValue()) {
                out.writeUTF(t.toMultiHash());
            }
        }

        out.writeInt(heads.size());
        for(Map.Entry<IGUID, IGUID> head : heads.entrySet()) {
            out.writeUTF(head.getKey().toMultiHash());
            out.writeUTF(head.getValue().toMultiHash());
        }

        out.writeInt(assetsToVersions.size());
        for(Map.Entry<IGUID, Set<IGUID>> versions : assetsToVersions.entrySet()) {
            out.writeUTF(versions.getKey().toMultiHash());
            out.writeInt(versions.getValue().size()); // Number of versions per invariant

            for(IGUID version:versions.getValue()) {
                out.writeUTF(version.toMultiHash());
            }
        }

        out.writeInt(typeToManifest.size());
        for(Map.Entry<ManifestType, Set<IGUID>> ti : typeToManifest.entrySet()) {
            out.writeUTF(ti.getKey().toString());
            out.writeInt(ti.getValue().size());

            for(IGUID invariants:ti.getValue()) {
                out.writeUTF(invariants.toMultiHash());
            }
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        try {
            tips = new HashMap<>();
            int tipsSize = in.readInt();
            for (int i = 0; i < tipsSize; i++) {
                IGUID invariant = GUIDFactory.recreateGUID(in.readUTF());
                tips.put(invariant, new LinkedHashSet<>());

                int numberOfTipsPerInvariant = in.readInt();
                for (int j = 0; j < numberOfTipsPerInvariant; j++) {
                    IGUID version = GUIDFactory.recreateGUID(in.readUTF());
                    tips.get(invariant).add(version);
                }
            }

            heads = new HashMap<>();
            int headsSize = in.readInt();
            for(int i = 0; i < headsSize; i++) {
                IGUID invariant = GUIDFactory.recreateGUID(in.readUTF());
                IGUID version = GUIDFactory.recreateGUID(in.readUTF());
                heads.put(invariant, version);
            }

            assetsToVersions = new HashMap<>();
            int assetsToVersionsSize = in.readInt();
            for (int i = 0; i < assetsToVersionsSize; i++) {
                IGUID invariant = GUIDFactory.recreateGUID(in.readUTF());
                assetsToVersions.put(invariant, new LinkedHashSet<>());

                int numberOfVersionsPerInvariant = in.readInt();
                for (int j = 0; j < numberOfVersionsPerInvariant; j++) {
                    String version = in.readUTF();
                    assetsToVersions.get(invariant).add(GUIDFactory.recreateGUID(version));
                }
            }

            typeToManifest = new HashMap<>();
            int typeToInvariantSize = in.readInt();
            for(int i = 0; i < typeToInvariantSize; i++) {
                ManifestType manifestType = ManifestType.get(in.readUTF());
                typeToManifest.put(manifestType, new LinkedHashSet<>());

                int numberOfInvariants = in.readInt();
                for(int j = 0; j < numberOfInvariants; j++) {
                    String invariant = in.readUTF();
                    typeToManifest.get(manifestType).add(GUIDFactory.recreateGUID(invariant));
                }
            }

        } catch (GUIDGenerationException e) {
            SOS_LOG.log(LEVEL.WARN, "Manifest cache loading - unable to recreated some of the GUIDs");
        }
    }
}
