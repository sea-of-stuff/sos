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
