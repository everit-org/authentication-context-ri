/**
 * This file is part of org.everit.osgi.authentication.context.ri.tests.
 *
 * org.everit.osgi.authentication.context.ri.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.context.ri.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.context.ri.tests.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.ri.tests;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.context.api.AuthenticationContext;
import org.everit.osgi.authentication.context.api.AuthenticationPropagator;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.props.PropertyService;
import org.junit.Assert;
import org.junit.Test;

@Component(name = "AuthenticationTest", immediate = true, configurationFactory = false,
        policy = ConfigurationPolicy.OPTIONAL)
@Properties({
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE, value = "junit4"),
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID, value = "AuthenticationTest"),
        @Property(name = "authenticationService.target"),
        @Property(name = "propertyService.target")
})
@Service(value = AuthenticationTestComponent.class)
public class AuthenticationTestComponent {

    private static final String MESSAGE = "the exception that tests the finally block in the runAs() method";

    @Reference
    private AuthenticationContext authenticationContext;

    @Reference
    private AuthenticationPropagator authenticationPropagator;

    @Reference
    private PropertyService propertyService;

    public void bindAuthenticationContext(final AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public void bindAuthenticationPropagator(final AuthenticationPropagator authenticationPropagator) {
        this.authenticationPropagator = authenticationPropagator;
    }

    public void bindPropertyService(final PropertyService ps) {
        propertyService = ps;
    }

    @Test
    public void testArgumentValidations() {
        try {
            authenticationPropagator.runAs(0, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("authenticatedAction cannot be null", e.getMessage());
        }
    }

    @Test
    public void testComplex() {
        String defaultResourceIdString = propertyService
                .getProperty(AuthenticationContext.PROP_DEFAULT_RESOURCE_ID);
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
