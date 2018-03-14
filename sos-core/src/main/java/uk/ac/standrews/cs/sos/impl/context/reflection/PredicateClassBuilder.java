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

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.text.WordUtils;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;

import java.io.IOException;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.JSONConstants.KEY_PREDICATE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PredicateClassBuilder implements ClassBuilder {

    private static final String NEW_LINE = "\n";

    private static final String PACKAGE_DECLARATION = "package " + ClassBuilderFactory.PACKAGE  + ";" + NEW_LINE;
    private static final String IMPORTEE_TAG = "_IMPORTEE_";
    private static final String IMPORT = "import " + IMPORTEE_TAG + ";" + NEW_LINE;
    private static final String CLASS_NAME_TAG = "_CLASS_NAME_";
    private static final String COMMON_CLASS = "BasePredicate";
    private static final String CLASS_SIGNATURE_TEMPLATE = "public class " + CLASS_NAME_TAG + " extends " + COMMON_CLASS + " {" + NEW_LINE;
    private static final String CLASS_CLOSING = "}";

    private static final String CONSTRUCTOR_BODY = "super(predicateManifest);";
    private static final String CONSTRUCTOR = "public " + CLASS_NAME_TAG + " (JsonNode predicateManifest) throws Exception {  "
            + NEW_LINE + CONSTRUCTOR_BODY + NEW_LINE + "}" + NEW_LINE;

    private static final String PREDICATE_TAG = "_PREDICATE_TAG_";
    private static final String PREDICATE_METHOD =
            "    @Override\n"+
            "    public boolean test(IGUID guid) {\n"+
            "\n"+
            "        return " + PREDICATE_TAG + "\n"+
            "    }" + NEW_LINE;

    @Override
    public String className(JsonNode jsonNode) throws IOException {

        IGUID predicateRef;
        try {
            predicateRef = GUIDFactory.generateGUID(GUID_ALGORITHM, jsonNode.toString());
        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to generate predicate ref from json node " + jsonNode.toString());
        }

        return WordUtils.capitalize(predicateRef.toMultiHash());
    }

    @Override
    public String constructClass(JsonNode jsonNode) throws IOException {

        String className = className(jsonNode);

        /////////////////////////
        // Package and Imports //
        /////////////////////////
        StringBuilder clazz = new StringBuilder(PACKAGE_DECLARATION);
        clazz.append(NEW_LINE);

        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.guid.IGUID"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.logger.LEVEL"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.impl.services.SOSAgent"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.NodesCollection"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.model.Policy"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "uk.ac.standrews.cs.sos.utils.SOS_LOG"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "java.util.Collections"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "java.util.Arrays"));
        clazz.append(IMPORT.replace(IMPORTEE_TAG, "com.fasterxml.jackson.databind.JsonNode"));
        clazz.append(NEW_LINE);

        //////////////////////////
        // Class & Constructors //
        //////////////////////////
        clazz.append(CLASS_SIGNATURE_TEMPLATE.replace(CLASS_NAME_TAG, className));
        clazz.append(NEW_LINE);

        clazz.append(CONSTRUCTOR.replace(CLASS_NAME_TAG, className));
        clazz.append(NEW_LINE);

        ///////////////
        // Predicate //
        ///////////////
        String predicateCode = jsonNode.get(KEY_PREDICATE).asText();
        clazz.append(PREDICATE_METHOD.replace(PREDICATE_TAG, predicateCode));

        clazz.append(CLASS_CLOSING);
        clazz.append(NEW_LINE);

        return clazz.toString();
    }
}
