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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.impl.metadata.MetaType;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.impl.metadata.Property;
import uk.ac.standrews.cs.sos.impl.services.SOSAgent;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.SecureManifest;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.List;

/**
 * This class contains useful Predicates that can be used by the contexts, as defined by the Context interface
 * @see uk.ac.standrews.cs.sos.model.Context
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonPredicates {

    private CommonPredicates() {}

    /**
     * Accept everything.
     *
     * @return true
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean AcceptAll() {

        return true;
    }

    /**
     * This method constructs a predicate that checks that the metadata of some given version matches any of the properties specified
     *
     * @param matchingContentTypes the content types that should be matched
     * @return the predicate
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean MetadataPropertyPredicate(IGUID guid, String property, List<String> matchingContentTypes) {

        SOSAgent agent = SOSAgent.instance();

        try {
            System.out.println("Getting metadata property: " + property);
            Property metaProperty = agent.getMetaProperty(guid, property);
            if (metaProperty.getType() == MetaType.STRING) {

                String contentType = metaProperty.getValue_s();
                return matchingContentTypes.contains(contentType);
            }

        } catch (Exception e) {
            // This could occur because the metadata could not be found or the type property was not available
            SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
        }

        return false;
    }

    public static boolean ContentTypePredicate(IGUID guid, List<String> matchingContentTypes) {

       return MetadataPropertyPredicate(guid, MetadataConstants.CONTENT_TYPE, matchingContentTypes);
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean MetadataIntPropertyPredicate(IGUID guid, String property, Integer matchingValue) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Property metaProperty = agent.getMetaProperty(guid, property);
            if (metaProperty.getType() == MetaType.LONG) {

                Long value = metaProperty.getValue_l();
                return value.intValue() == matchingValue;
            }

        } catch (Exception e) {
            // This could occur because the metadata could not be found or the type property was not available
            SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
        }

        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean MetadataIntGreaterPropertyPredicate(IGUID guid, String property, Integer matchingValue) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Property metaProperty = agent.getMetaProperty(guid, property);
            if (metaProperty.getType() == MetaType.LONG) {

                Long value = metaProperty.getValue_l();
                return value.intValue() > matchingValue;
            }

        } catch (Exception e) {
            // This could occur because the metadata could not be found or the type property was not available
            SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
        }

        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean MetadataIntLessPropertyPredicate(IGUID guid, String property, Integer matchingValue) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Property metaProperty = agent.getMetaProperty(guid, property);
            if (metaProperty.getType() == MetaType.LONG) {

                Long value = metaProperty.getValue_l();
                return value.intValue() < matchingValue;
            }

        } catch (Exception e) {
            // This could occur because the metadata could not be found or the type property was not available
            SOS_LOG.log(LEVEL.WARN, "Unable to find content type");
        }

        return false;
    }

    /**
     * Check if the manifest with the given GUID is signed by a Role matching the signer GUID
     * @param guid
     * @param signer
     * @return
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean SignedBy(IGUID guid, IGUID signer) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Property property = agent.getMetaProperty(guid, "signer");
            if (property.getType() == MetaType.GUID) {

                IGUID signerFound = property.getValue_g();
                return signerFound.equals(signer);
            }

        } catch (ServiceException e) {
            SOS_LOG.log(LEVEL.WARN, "Unable to find signer");
        }

        return false;
    }

    public static boolean ContentIsProtected(IGUID guid) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Manifest manifest = agent.getManifest(guid);

            if (manifest instanceof Version) {
                Manifest contentManifest = agent.getManifest(((Version) manifest).content());
                return contentManifest instanceof SecureManifest;
            }

            return false;

        } catch (ServiceException e) {

            return false;
        }

    }

    public static boolean ContentIsNotProtected(IGUID guid) {

        return !ContentIsProtected(guid);
    }

    public static boolean SearchText(IGUID guid, String textToSearch) {

        SOSAgent agent = SOSAgent.instance();

        try (Data data = agent.getData(guid)){

            return stringUtilsWithLimits(data.toString(), textToSearch, 1) == 1;

        } catch (ServiceException | IOException e) {
            return false;
        }
    }

    public static boolean SearchTextIgnoreCase(IGUID guid, String textToSearch) {

        SOSAgent agent = SOSAgent.instance();

        try (Data data = agent.getData(guid)){

            return stringUtilsWithLimits(data.toString().toLowerCase(), textToSearch.toLowerCase(), 1) == 1;

        } catch (ServiceException | IOException e) {
            return false;
        }

    }

    public static int TextOccurrences(IGUID guid, String textToSearch) {

        SOSAgent agent = SOSAgent.instance();

        try (Data data = agent.getData(guid)){

            return stringUtilsWithLimits(data.toString(), textToSearch, -1);

        } catch (ServiceException | IOException e) {
            return 0;
        }

    }

    public static int TextOccurrencesIgnoreCase(IGUID guid, String textToSearch) {

        SOSAgent agent = SOSAgent.instance();

        try (Data data = agent.getData(guid)){

            return stringUtilsWithLimits(data.toString().toLowerCase(), textToSearch.toLowerCase(), -1);

        } catch (ServiceException | IOException e) {
            return 0;
        }

    }

    // Improvement over method StringUtils.countMatches from commons.lang3
    private static int stringUtilsWithLimits(String str, String sub, int limit) {

        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            if (limit != -1 && count == limit) break;
            idx += sub.length();
        }
        return count;
    }

    private static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean JavaFileHasMethod(IGUID guid, String method) {

        SOSAgent agent = SOSAgent.instance();

        try (Data data = agent.getData(guid)){

            CompilationUnit compilationUnit = JavaParser.parse(data.toString());
            return InspectJavaFileForMethod(compilationUnit.getChildNodes(), method);

        } catch (ServiceException | IOException e) {
            return false;
        }

    }

    public static boolean JavaFileHasClass(IGUID guid, String clazz) {

        SOSAgent agent = SOSAgent.instance();

        try (Data data = agent.getData(guid)){

            CompilationUnit compilationUnit = JavaParser.parse(data.toString());
            return InspectJavaForClass(compilationUnit.getChildNodes(), clazz);

        } catch (ServiceException | IOException e) {
            return false;
        }

    }

    private static boolean InspectJavaFileForMethod(List<Node> nodes, String methodName) {

        for(Node node:nodes) {

            if (node instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) node;
                if (methodDeclaration.getName().asString().equals(methodName)) {
                    return true;
                }
            }

            boolean retval = InspectJavaFileForMethod(node.getChildNodes(), methodName);
            if (retval) {
                return true;
            }
        }

        return false;
    }

    private static boolean InspectJavaForClass(List<Node> nodes, String className) {

        for(Node node:nodes) {

            if (node instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
                if (!classOrInterfaceDeclaration.isInterface()) {
                    if (classOrInterfaceDeclaration.getName().asString().equals(className)) {
                        return true;
                    }
                }
            }

            boolean retval = InspectJavaForClass(node.getChildNodes(), className);
            if (retval) {
                return true;
            }
        }

        return false;
    }
}


/*
 ## Ideas for advanced predicates

 - can get geo-location using the service ipinfo.io
 example:
 curl ipinfo.io
 curl ipinfo.io/138.250.10.10

 more here: https://ipinfo.io/developers
 */