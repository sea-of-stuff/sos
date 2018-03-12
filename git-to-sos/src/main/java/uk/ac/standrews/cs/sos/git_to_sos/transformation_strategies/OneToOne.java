package uk.ac.standrews.cs.sos.git_to_sos.transformation_strategies;

import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.git_to_sos.dag.interfaces.*;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.*;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Commit --> Version
 * Tree --> Compound
 * Blob --> Atom
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class OneToOne extends BaseStrategy {

    public OneToOne(SOSLocalNode node, DAG dag) {
        super(node, dag);
    }

    public void transform() {

        blobsToAtoms();
        treesToCompound();
        commitsToVersions();
    }

    private void blobsToAtoms() {
        System.out.println("Blobs --> Atoms");

        for(Map.Entry<String, Blob> entry:dag.getBlobs().entrySet()) {

            try {
                AtomBuilder atomBuilder = new AtomBuilder()
                        .setData(new InputStreamData(entry.getValue().getData()));
                Atom atom = node.getAgent().addAtom(atomBuilder);

                blobToSOS.put(entry.getKey(), atom.guid().toMultiHash());
                all.put(entry.getKey(), atom.guid());

            } catch (ServiceException e) {
                e.printStackTrace();
            }

        }
    }

    private void treesToCompound() {
        System.out.println("Trees --> Compounds");

        for(Map.Entry<String, Tree> entry:dag.getTrees().entrySet()) {

            Set<Content> contents = new LinkedHashSet<>();

            // Re-Map tree contents
            for(Map.Entry<String, Entity> treeContent:entry.getValue().getContents().entrySet()) {
                String name = treeContent.getKey();
                String id = treeContent.getValue().getId();
                IGUID contentGUID = all.get(id);
                contents.add(new ContentImpl(name, contentGUID));
            }

            // Add compound
            try {
                CompoundBuilder compoundBuilder = new CompoundBuilder()
                        .setType(CompoundType.COLLECTION)
                        .setContents(contents);
                Compound compound = node.getAgent().addCompound(compoundBuilder);

                treesToSOS.put(entry.getKey(), compound.guid().toMultiHash());
                all.put(entry.getKey(), compound.guid());

            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }

    private void commitsToVersions() {
        System.out.println("Commits --> Versions");

        Commit currentCommit = dag.getRoot();
        Commit prevCommit = null; // TODO - manage multiple prev commits
        do {

            try {
                String treeId = currentCommit.getTree().getId();
                IGUID treeGUID = all.get(treeId);

                VersionBuilder versionBuilder = new VersionBuilder()
                        .setContent(treeGUID);

                if (prevCommit != null) {

                    Set<IGUID> prevs = new LinkedHashSet<>();
                    String prevVersionId = commitsToSOS.get(prevCommit.getId());
                    IGUID prev = all.get(prevVersionId);
                    prevs.add(prev);

                    versionBuilder.setPrevious(prevs);
                }

                Version version = node.getAgent().addVersion(versionBuilder);
                commitsToSOS.put(currentCommit.getId(), version.guid().toMultiHash());
                all.put(currentCommit.getId(), version.guid());

                prevCommit = currentCommit;
                currentCommit = currentCommit.getNext() != null ? currentCommit.getNext().iterator().next() : null;

            } catch (ServiceException e) {
                e.printStackTrace();
            }

        } while(currentCommit != null);
    }

}
