package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsIndex;
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
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsIndexImpl implements ManifestsIndex, Serializable {

    // [type --> invariant]
    private transient HashMap<ManifestType, Set<IGUID>> typeToInvariant;
    // [invariant --> [versionable]]
    private transient HashMap<IGUID, Set<IGUID>> assetsToVersions;
    // [invariant --> [versionable/tip]]
    private transient HashMap<IGUID, Set<IGUID>> tips;
    // [invariant --> versionable/head]
    private transient HashMap<IGUID, IGUID> heads;

    public ManifestsIndexImpl() {

        tips = new HashMap<>();
        heads = new HashMap<>();
        assetsToVersions = new HashMap<>();
        typeToInvariant = new HashMap<>();
    }

    @Override
    public Set<IGUID> getInvariants(ManifestType type) {

        if (type != ManifestType.CONTEXT && type != ManifestType.VERSION) {
            return new LinkedHashSet<>();
        }

        if (typeToInvariant.containsKey(type)) {
            return typeToInvariant.get(type);
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

        IGUID invariantGUID = versionable.invariant();
        IGUID versionGUID = versionable.guid();

        heads.put(invariantGUID, versionGUID);

        // Update the [invariant --> [version]] map
        if (!assetsToVersions.containsKey(invariantGUID)) {
            assetsToVersions.put(invariantGUID, new LinkedHashSet<>());
        }
        assetsToVersions.get(invariantGUID).add(versionGUID);
    }

    @Override
    public void advanceTip(Versionable versionable) {

        Set<IGUID> previousVersions = versionable.previous();

        if (previousVersions == null || previousVersions.isEmpty()) {
            advanceTip(versionable.invariant(), versionable.guid());
        } else {
            advanceTip(versionable.invariant(), versionable.previous(), versionable.guid());
        }

        // Add the invariant of this version to the index
        if (!typeToInvariant.containsKey(versionable.getType())) {
            typeToInvariant.put(versionable.getType(), new LinkedHashSet<>());
        }
        typeToInvariant.get(versionable.getType()).add(versionable.invariant());
    }

    @Override
    public void flush() {

    }

    @Override
    public void rebuild() {

        // TODO - FORCE THE INDEX TO BE REBUILT FROM THE LOCAL DIRECTORY
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

        out.writeInt(typeToInvariant.size());
        for(Map.Entry<ManifestType, Set<IGUID>> ti : typeToInvariant.entrySet()) {
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
                    String version = in.readUTF();
                    tips.get(invariant).add(GUIDFactory.recreateGUID(version));
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

            typeToInvariant = new HashMap<>();
            int typeToInvariantSize = in.readInt();
            for(int i = 0; i < typeToInvariantSize; i++) {
                ManifestType manifestType = ManifestType.get(in.readUTF());
                typeToInvariant.put(manifestType, new LinkedHashSet<>());

                int numberOfInvariants = in.readInt();
                for(int j = 0; j < numberOfInvariants; j++) {
                    String invariant = in.readUTF();
                    typeToInvariant.get(manifestType).add(GUIDFactory.recreateGUID(invariant));
                }
            }

        } catch (GUIDGenerationException e) {
            SOS_LOG.log(LEVEL.WARN, "Manifest cache loading - unable to recreated some of the GUIDs");
        }
    }
}
