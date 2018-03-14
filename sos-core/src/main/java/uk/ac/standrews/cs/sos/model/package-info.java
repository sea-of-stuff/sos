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
/**
 * First class entities of the SOS Model.
 * The classes in the model package define the entire SOS Model.
 *
 * The implementation of the model is to be found outside of this package.
 * The SOS Architecture and internal data structures are defined outside of this package.
 *
 * The model consists of manifests, contexts, usro:
 *
 * Manifests:
 * - data ones:
 *  - Atom
 *  - Compound
 *  - Version
 *  - Metadata
 *
 * - protected ones:
 *  - SecureAtom
 *  - SecureCompound
 *  - SecureMetadata
 *
 *
 * - Contexts
 *  - Context
 *  - Predicate
 *  - Policy
 *
 *
 * - USRO
 *  - User
 *  - Role
 *
 *
 * - Node
 *
 *
 * The NodesCollection defines different collections of SOS nodes
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
package uk.ac.standrews.cs.sos.model;
