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
package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.impl.context.CommonUtilities;
import uk.ac.standrews.cs.sos.impl.json.PolicyDeserializer;
import uk.ac.standrews.cs.sos.impl.json.PolicySerializer;

/**
 * A policy is a task apply on the content of a given context.
 * Policies are used to enforce control over content of a given context.
 *
 * Examples:
 * - replicate data to nodes [X]
 * - replicate data at least N times
 * - protect data
 * - migrate data from S3 to Azure
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = PolicySerializer.class)
@JsonDeserialize(using = PolicyDeserializer.class)
public interface Policy extends ComputationalUnit {

    /**
     * Run this policy over a manifest
     *
     * @param codomain over which to apply the policy
     * @param commonUtilities
     * @param manifest over which this policy runs
     * @throws PolicyException if an error occurred while applying the policy
     * @see #satisfied(NodesCollection, CommonUtilities, Manifest) for info about the success of the policy
     */
    void apply(NodesCollection codomain, CommonUtilities commonUtilities, Manifest manifest) throws PolicyException;

    /**
     * Check that the policy is satisfied
     *
     * @param codomain over which the policy must be satisfied
     * @param commonUtilities
     * @param manifest over which this policy will satisfied its agreement
     * @return true if the policy is satisfied
     * @throws PolicyException if an error occurred while checking that the policy is satisfied
     */
    boolean satisfied(NodesCollection codomain, CommonUtilities commonUtilities, Manifest manifest) throws PolicyException;

    JsonNode apply();

    JsonNode satisfied();

    JsonNode fields();
}
