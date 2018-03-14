/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module git-to-sos.
 *
 * git-to-sos is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * git-to-sos is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with git-to-sos. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
                System.out.println("Added atom --> " + atom.guid().toShortString());

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
                System.out.println("Added compound --> " + compound.guid().toShortString());

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
        IGUID invariant = null;
        do {

            try {
                String treeId = currentCommit.getTree().getId();
                IGUID treeGUID = all.get(treeId);

                VersionBuilder versionBuilder = new VersionBuilder()
                        .setContent(treeGUID);

                if (prevCommit != null) {

                    Set<IGUID> prevs = new LinkedHashSet<>();
                    IGUID prev = all.get(prevCommit.getId());
                    prevs.add(prev);

                    versionBuilder.setPrevious(prevs);
                }

                if (invariant != null) {

                    versionBuilder.setInvariant(invariant);
                }

                Version version = node.getAgent().addVersion(versionBuilder);
                invariant = version.invariant();
                System.out.println("Added version --> " + version.guid().toShortString());

                commitsToSOS.put(currentCommit.getId(), version.guid().toMultiHash());
                all.put(currentCommit.getId(), version.guid());

                prevCommit = currentCommit;
                currentCommit = currentCommit.getNext() != null && currentCommit.getNext().iterator().hasNext() ? currentCommit.getNext().iterator().next() : null;

            } catch (ServiceException e) {
                e.printStackTrace();
            }

        } while(currentCommit != null);
    }

}
