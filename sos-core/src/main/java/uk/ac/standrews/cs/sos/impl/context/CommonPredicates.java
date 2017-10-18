package uk.ac.standrews.cs.sos.impl.context;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.apache.commons.lang3.StringUtils;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataConstants;
import uk.ac.standrews.cs.sos.impl.services.SOSAgent;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.SecureManifest;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

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
            String contentType = (String) agent.getMetaProperty(guid, property);
            return matchingContentTypes.contains(contentType);

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
            Integer value = (Integer) agent.getMetaProperty(guid, property);
            return value.equals(matchingValue);

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
            Integer value = (Integer) agent.getMetaProperty(guid, property);
            return value.compareTo(matchingValue) > 0;

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
            Integer value = (Integer) agent.getMetaProperty(guid, property);
            return value.compareTo(matchingValue) < 0;

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
            IGUID signerFound = GUIDFactory.recreateGUID((String) agent.getMetaProperty(guid, "signer")); // FIXME - use appropriate method from Metadata obj
            return signerFound.equals(signer);

        } catch (ServiceException | GUIDGenerationException e) {

            return false;
        }

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

        try {
            Data data = agent.getData(guid);
            return data.toString().contains(textToSearch);

        } catch (ServiceException e) {

            return false;
        }
    }

    public static boolean SearchTextIgnoreCase(IGUID guid, String textToSearch) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Data data = agent.getData(guid);
            return data.toString().toLowerCase().contains(textToSearch);

        } catch (ServiceException e) {

            return false;
        }
    }

    public static int TextOccurrences(IGUID guid, String textToSearch) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Data data = agent.getData(guid);
            return StringUtils.countMatches(data.toString(), textToSearch);
        } catch (ServiceException e) {

            return 0;
        }
    }

    public static int TextOccurrencesIgnoreCase(IGUID guid, String textToSearch) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Data data = agent.getData(guid);
            return StringUtils.countMatches(data.toString().toLowerCase(), textToSearch);
        } catch (ServiceException e) {

            return 0;
        }
    }

    public static boolean JavaFileHasMethod(IGUID guid, String method) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Data data = agent.getData(guid);

            CompilationUnit compilationUnit = JavaParser.parse(data.toString());
            return InspectJavaFileForMethod(compilationUnit.getChildNodes(), method);

        } catch (ServiceException e) {

            return false;
        }

    }

    public static boolean JavaFileHasClass(IGUID guid, String clazz) {

        SOSAgent agent = SOSAgent.instance();

        try {
            Data data = agent.getData(guid);

            CompilationUnit compilationUnit = JavaParser.parse(data.toString());
            return InspectJavaForClass(compilationUnit.getChildNodes(), clazz);

        } catch (ServiceException e) {

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


/**

 ## Ideas for advanced predicates

 - can get geo-location using the service ipinfo.io
 example:
 curl ipinfo.io
 curl ipinfo.io/138.250.10.10

 more here: https://ipinfo.io/developers
 */