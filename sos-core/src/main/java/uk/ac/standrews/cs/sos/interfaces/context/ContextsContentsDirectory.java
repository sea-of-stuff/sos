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
package uk.ac.standrews.cs.sos.interfaces.context;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.context.directory.ContextVersionInfo;

import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ContextsContentsDirectory {

    void addOrUpdateEntry(IGUID contextInvariant, IGUID version, ContextVersionInfo contextVersionInfo);

    void evict(IGUID context, IGUID version);

    void delete(IGUID context, IGUID version);

    void delete(IGUID context);

    ContextVersionInfo getEntry(IGUID context, IGUID version);

    boolean entryExists(IGUID context, IGUID version);

    Map<IGUID, ContextVersionInfo> getContentsThatPassedPredicateTestRows(IGUID context, boolean includeEvicted);

    void clear();

}
