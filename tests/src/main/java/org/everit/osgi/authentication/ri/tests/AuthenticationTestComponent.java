/**
 * This file is part of org.everit.osgi.authentication.ri.tests.
 *
 * org.everit.osgi.authentication.ri.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.ri.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.ri.tests.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.ri.tests;

import java.util.Random;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.api.AuthenticatedAction;
import org.everit.osgi.authentication.api.AuthenticationService;
import org.everit.osgi.authentication.api.Subject;
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
    private AuthenticationService authenticationService;

    @Reference
    private PropertyService propertyService;

    private final Subject subject = new Subject() {

        private final long resourceId = new Random().nextLong();

        @Override
        public long getResourceId() {
            return resourceId;
        }

    };

    public void bindAuthenticationService(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void bindPropertyService(final PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Test
    public void testArgumentValidations() {
        try {
            authenticationService.logout(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("subject cannot be null", e.getMessage());
        }
        try {
            authenticationService.runAs(null, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("subject cannot be null", e.getMessage());
        }
        try {
            authenticationService.runAs(subject, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("authenticatedAction cannot be null", e.getMessage());
        }
    }

    @Test
    public void testComplex() {
        String defaultSubjectResourceIdString = propertyService
                .getProperty(AuthenticationService.PROP_DEFAULT_SUBJECT_RESOURCE_ID);
        long defaultSubjectResourceId = Long.valueOf(defaultSubjectResourceIdString);

        Subject currentSubject = authenticationService.getCurrentSubject();
        Assert.assertEquals(defaultSubjectResourceId, currentSubject.getResourceId());
        authenticationService.runAs(subject, new AuthenticatedAction<Object>() {

            @Override
            public Object run() {
                Subject currentSubject = authenticationService.getCurrentSubject();
                Assert.assertEquals(subject.getResourceId(), currentSubject.getResourceId());
                return null;
            }

        });
        currentSubject = authenticationService.getCurrentSubject();
        Assert.assertEquals(defaultSubjectResourceId, currentSubject.getResourceId());

        try {
            authenticationService.runAs(subject, new AuthenticatedAction<Object>() {

                @Override
                public Object run() {
                    throw new NullPointerException(MESSAGE);
                }

            });
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals(MESSAGE, e.getMessage());
        }
        currentSubject = authenticationService.getCurrentSubject();
        Assert.assertEquals(defaultSubjectResourceId, currentSubject.getResourceId());
    }

    @Test
    public void testLogout() {
        try {
            authenticationService.logout(subject);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
    }

}
