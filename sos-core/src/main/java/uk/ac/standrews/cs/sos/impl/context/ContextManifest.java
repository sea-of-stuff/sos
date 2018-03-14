/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.constants.Internals;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.manifest.AbstractSignedManifest;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextManifest extends AbstractSignedManifest implements Context {

    private Instant timestamp;
    private String name;
    private IGUID guid;
    private IGUID invariant;
    private IGUID previous;
    private IGUID predicate;
    private long maxAge;
    private Set<IGUID> policies;
    private NodesCollection domain;
    private NodesCollection codomain;
    private IGUID content;

    /**
     * The predicate is true only at the time when it is computed.
     */
    public static final long PREDICATE_ALWAYS_TO_COMPUTE = 0;

    /**
     * Use this constructor when creating an already existing object with its GUID known already
     *
     */
    public ContextManifest(String name, NodesCollection domain, NodesCollection codomain,
                           IGUID predicate, long maxAge, Set<IGUID> policies,
                           Role signer,
                           IGUID content) throws ManifestNotMadeException {
        super(ManifestType.CONTEXT, signer);

        this.timestamp = Instant.now();
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.predicate = predicate;
        this.maxAge = maxAge;
        this.policies = policies;
        this.content = content;

        this.previous = new InvalidID();

        this.invariant = makeInvariantGUID();
        this.guid = makeGUID();

        if (invariant.isInvalid() || guid.isInvalid()) {
            throw new ManifestNotMadeException("Unable to create the context manifest");
        }

        try {
            this.signature = makeSignature();
        } catch (SignatureException e) {
            throw new ManifestNotMadeException("Unable to sign the manifest");
        }
    }

    public ContextManifest(String name, NodesCollection domain, NodesCollection codomain,
                           IGUID predicate, long maxAge, Set<IGUID> policies,
                           IGUID signerRef,
                           IGUID content) throws ManifestNotMadeException {
        super(ManifestType.CONTEXT, signerRef);

        this.timestamp = Instant.now();
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.predicate = predicate;
        this.maxAge = maxAge;
        this.policies = policies;
        this.content = content;

        this.previous = new InvalidID();

        this.invariant = makeInvariantGUID();
        this.guid = makeGUID();

        if (invariant.isInvalid() || guid.isInvalid()) {
            throw new ManifestNotMadeException("Unable to create the context manifest");
        }

        try {
            this.signature = makeSignature();
        } catch (SignatureException e) {
            throw new ManifestNotMadeException("Unable to sign the manifest");
        }
    }

    public ContextManifest(Instant timestamp, String name, NodesCollection domain, NodesCollection codomain,
                           IGUID predicate, long maxAge, Set<IGUID> policies,
                           Role signer,
                           IGUID content, IGUID invariant, IGUID previous) throws ManifestNotMadeException {
        super(ManifestType.CONTEXT, signer);

        this.timestamp = timestamp;
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.predicate = predicate;
        this.maxAge = maxAge;
        this.policies = policies;
        this.content = content;

        this.previous = previous;

        this.invariant = makeInvariantGUID();
        this.guid = makeGUID();

        if (invariant.isInvalid() || guid.isInvalid()) {
            throw new ManifestNotMadeException("Unable to create the context manifest");
        }

        if (!this.invariant.equals(invariant)) {
            throw new ManifestNotMadeException("Invariant provided does not match the rest of the parameters for the context");
        }

        try {
            this.signature = makeSignature();
        } catch (SignatureException e) {
            throw new ManifestNotMadeException("Unable to sign the manifest");
        }
    }

    public ContextManifest(Instant timestamp, IGUID guid,
                           String name, NodesCollection domain, NodesCollection codomain,
                           IGUID predicate, long maxAge, Set<IGUID> policies,
                           Role signer, String signature,
                           IGUID content, IGUID invariant, IGUID previous) {
        super(ManifestType.CONTEXT, signer);

        this.timestamp = timestamp;
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.predicate = predicate;
        this.maxAge = maxAge;
        this.policies = policies;
        this.content = content;

        this.guid = guid;
        this.invariant = invariant;
        this.previous = previous;

        this.signature = signature;
    }

    public ContextManifest(Instant timestamp, IGUID guid,
                           String name, NodesCollection domain, NodesCollection codomain,
                           IGUID predicate, long maxAge, Set<IGUID> policies,
                           IGUID signerRef, String signature,
                           IGUID content, IGUID invariant, IGUID previous) {
        super(ManifestType.CONTEXT, signerRef);

        this.timestamp = timestamp;
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.predicate = predicate;
        this.maxAge = maxAge;
        this.policies = policies;
        this.content = content;

        this.guid = guid;
        this.invariant = invariant;
        this.previous = previous;

        this.signature = signature;
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public IGUID invariant() {

        return invariant;
    }

    @Override
    public IGUID content() {

        // GUID to compound of contents
        return content;
    }

    @Override
    public InputStream contentToHash() {

        String contentToHash = getType() +
                "N" + getName() +
                "I" + invariant().toMultiHash();

        if (previous != null && !previous.isInvalid()) {
            contentToHash += "P" + previous.toMultiHash();
        }

        contentToHash += "C" + content().toMultiHash();
        contentToHash += "DO" + domain().toUniqueString();
        contentToHash += "CO" + codomain().toUniqueString();

        return IO.StringToInputStream(contentToHash);
    }

    @Override
    public Set<IGUID> previous() {
        Set<IGUID> prev = new LinkedHashSet<>();
        if (!previous.isInvalid()) {
            prev.add(previous);
        }

        return prev;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUniqueName() {
        return name + "-" + guid.toMultiHash();
    }

    @Override
    public NodesCollection domain() {
        domain.shuffle();
        return domain;
    }

    @Override
    public NodesCollection codomain() {
        codomain.shuffle();
        return codomain;
    }

    @Override
    public IGUID predicate() {
        return predicate;
    }

    @Override
    public long maxAge() {
        return maxAge;
    }

    @Override
    public Instant timestamp() {
        return timestamp;
    }

    @Override
    public Set<IGUID> policies() {
        return policies;
    }

    @Override
    public String toFATString(Predicate predicate, Set<Policy> policies) throws IOException {

        return ContextBuilder.toFATString(this, predicate, policies);
    }

    /**
     * Method to make the invariant of the context.
     * The elements that make the invariant of a context cannot mutate between versions.
     * The elements are:
     * - type of the manifest itself
     * - the name of the context
     * - the predicate
     * - the policies
     * - the max-age property of the context
     */
    private IGUID makeInvariantGUID() {

        String contentToHash = getType() +
                "PR" + predicate().toMultiHash();

        Set<IGUID> policies = policies();
        if (policies != null && !policies.isEmpty()) {
            contentToHash += "PO" + getCollectionToHashOrSign(policies);
        }

        contentToHash += "MA" + Long.toString(maxAge);

        IGUID guid;
        try {
            long start = System.nanoTime();
            guid = GUIDFactory.generateGUID(GUID_ALGORITHM, contentToHash);
            long duration = System.nanoTime() - start;

            StatsTYPE subtype = StatsTYPE.getHashType(Internals.GUID_ALGORITHM);
            InstrumentFactory.instance().measure(StatsTYPE.guid_manifest, subtype, Long.toString(contentToHash.length()), duration);
        } catch (GUIDGenerationException e) {
            guid = new InvalidID();
        }

        return guid;
    }

}
