package uk.ac.standrews.cs.sos;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@PowerMockIgnore({"javax.net.ssl.*", "javax.crypto.*"})
public abstract class CommonTest extends PowerMockTestCase {

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        System.out.println("==STARTING Test: " + testMethod.getName() + ".");
    }

    @AfterMethod
    public void tearDown() throws Exception {}

    @AfterMethod // (dependsOnMethods = "tearDown")
    public void tearDownFinished(ITestResult result) {

        System.out.println("==FINISHED Test: " + result.getMethod().getMethodName() + ". STATUS: " + status(result.getStatus()) + ".\n");
    }

    private String status(int status) {
        switch(status) {
            case 1:
                return "SUCCESS";
            default:
                return "FAILED";
        }
    }
}
