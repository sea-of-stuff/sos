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
package uk.ac.standrews.cs.sos.impl.context.reflection;

import uk.ac.standrews.cs.sos.exceptions.reflection.ClassBuilderException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClassBuilderFactory {

    static final String PACKAGE = "uk.ac.standrews.cs.sos.impl.context";

    public static ClassBuilder getClassBuilder(String builder) throws ClassBuilderException {

        switch(builder.toLowerCase()) {

            case "predicate" :
                return new PredicateClassBuilder();

            case "policy" :
                return new PolicyClassBuilder();

            default:
                throw new ClassBuilderException("Unknown class builder type");
        }
    }
}
