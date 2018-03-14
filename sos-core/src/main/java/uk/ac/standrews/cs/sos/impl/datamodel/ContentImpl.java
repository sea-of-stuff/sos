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
package uk.ac.standrews.cs.sos.impl.datamodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.impl.json.ContentDeserializer;
import uk.ac.standrews.cs.sos.impl.json.ContentSerializer;
import uk.ac.standrews.cs.sos.model.Content;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Objects;

/**
 * Envelope class used to represent some content with metadata associated with it.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = ContentSerializer.class)
@JsonDeserialize(using = ContentDeserializer.class)
public class ContentImpl implements Content {

    final private IGUID guid;
    private String label;

    /**
     * Constructs a content envelope using a GUID.
     *
     * @param guid
     */
    public ContentImpl(IGUID guid) {
        this.guid = guid;
    }

    /**
     * Constructs a content envelope using a GUID and a label.
     * (e.g. label - "holidays").
     *
     * @param label
     * @param guid
     */
    public ContentImpl(String label, IGUID guid) {
        this(guid);
        this.label = label;
    }

    @Override
    public IGUID getGUID() {
        return guid;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        try {
            return JSONHelper.jsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to generate JSON for content object " + this);
            return "";
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentImpl content = (ContentImpl) o;
        return Objects.equals(guid, content.guid) &&
                Objects.equals(label, content.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, label);
    }
}
