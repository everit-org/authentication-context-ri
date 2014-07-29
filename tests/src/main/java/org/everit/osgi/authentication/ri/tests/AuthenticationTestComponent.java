/**
 * This file is part of Everit - Authentication Context RI Tests.
 *
 * Everit - Authentication Context RI Tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Authentication Context RI Tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Authentication Context RI Tests.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.ri.tests;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.context.AuthenticationContext;
import org.everit.osgi.authentication.context.AuthenticationPropagator;
import org.everit.osgi.authentication.context.ri.AuthenticationContextConstants;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.props.PropertyManager;
import org.junit.Assert;
import org.junit.Test;

@Component(name = "AuthenticationTest", immediate = true, configurationFactory = false,
        policy = ConfigurationPolicy.OPTIONAL)
@Properties({
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE, value = "junit4"),
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID, value = "AuthenticationTest"),
        @Property(name = "authenticationService.target"),
        @Property(name = "propertyManager.target")
})
@Service(value = AuthenticationTestComponent.class)
public class AuthenticationTestComponent {

    private static final String MESSAGE = "the exception that tests the finally block in the runAs() method";

    @Reference(bind = "setAuthenticationContext")
    private AuthenticationContext authenticationContext;

    @Reference(bind = "setAuthenticationPropagator")
    private AuthenticationPropagator authenticationPropagator;

    @Reference(bind = "setPropertyManager")
    private PropertyManager propertyManager;

    public void setAuthenticationContext(final AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public void setAuthenticationPropagator(final AuthenticationPropagator authenticationPropagator) {
        this.authenticationPropagator = authenticationPropagator;
    }

    public void setPropertyManager(final PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    @Test
    public void testArgumentValidations() {
        try {
            authenticationPropagator.runAs(0, null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("authenticatedAction cannot be null", e.getMessage());
        }
    }

    @Test
    public void testComplex() {
        String defaultResourceIdString =
                propertyManager.getProperty(AuthenticationContextConstants.PROP_DEFAULT_RESOURCE_ID);
        long defaultResourceId = Long.valueOf(defaultResourceIdString);

        Assert.assertEquals(defaultResourceId, authenticationContext.getCurrentResourceId());

        authenticationPropagator.runAs(1, () -> {
            Assert.assertEquals(1, authenticationContext.getCurrentResourceId());
            return null;
        });
        Assert.assertEquals(defaultResourceId, authenticationContext.getCurrentResourceId());

        try {
            authenticationPropagator.runAs(1, () -> {
                throw new NullPointerException(MESSAGE);
            });
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals(MESSAGE, e.getMessage());
        }
        Assert.assertEquals(defaultResourceId, authenticationContext.getCurrentResourceId());

        authenticationPropagator.runAs(1, () -> {
            Assert.assertEquals(1, authenticationContext.getCurrentResourceId());
            authenticationPropagator.runAs(2, () -> {
                Assert.assertEquals(2, authenticationContext.getCurrentResourceId());
                return null;
            });
            Assert.assertEquals(1, authenticationContext.getCurrentResourceId());
            return null;
        });
        Assert.assertEquals(defaultResourceId, authenticationContext.getCurrentResourceId());
    }

}
