package uk.ac.standrews.cs.sos.configurations;

import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaConfiguration {

    private static final String HOME = System.getProperty("user.home");

    private static GUID machineid;
    private static String root;
    private static String data;
    private static String cachedData;
    private static String index;
    private static String manifests;
    private static String privateKeyFile;
    private static String publicKeyFile;

    // XXX - other configs, such as #threads running, etc, could be useful

    private static SeaConfiguration instance;

    public static SeaConfiguration getInstance() throws IOException {
        if(instance == null) {
            try {
                loadConfiguration();
            } catch (SeaConfigurationException e) {
                loadFakeConfiguration();
            }
            instance = new SeaConfiguration();
        }
        return instance;
    }

    private static void loadFakeConfiguration() {
        machineid = new GUIDsha1("abcdefg12345");
        root = "/sos/test/";
        data = "data/";
        cachedData = "cached_data/";
        index = "index/";
        manifests = "manifests/";
        privateKeyFile = "keys/private.der";
        publicKeyFile = "keys/public.der";
    }

    private static void loadConfiguration() throws SeaConfigurationException {
        try (BufferedReader reader = new BufferedReader(new FileReader(HOME + "/config.txt")) ){
            machineid = new GUIDsha1(reader.readLine());
            root = reader.readLine();
            data = reader.readLine();
            cachedData = reader.readLine();
            index = reader.readLine();
            manifests = reader.readLine();
            privateKeyFile = reader.readLine();
            publicKeyFile = reader.readLine();
        } catch (IOException e) {
            throw new SeaConfigurationException();
        }
    }

    public GUID getMachineID() {
     return machineid;
    }

    public String getDataPath() {
        return HOME + root + data;
    }

    public String getLocalManifestsLocation() {
        return HOME + root + manifests;
    }

    public String[] getIdentityPaths() {
         return new String[] { HOME + root + privateKeyFile,
                 HOME + root + publicKeyFile };
    }

    public String getIndexPath() {
         return HOME + root + index;
    }

    public String getCacheDataPath() {
        return HOME + root + cachedData;
    }


}
