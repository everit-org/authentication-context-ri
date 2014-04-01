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
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.ri.ImmutableSubject;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.junit.Assert;
import org.junit.Test;

@Component(name = "ImmutableSubjectTest", immediate = true, configurationFactory = false,
        policy = ConfigurationPolicy.OPTIONAL)
@Properties({
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE, value = "junit4"),
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID, value = "ImmutableSubjectTest")
})
@Service(value = ImmutableSubjectTestComponent.class)
public class ImmutableSubjectTestComponent {

    private final long resourceId = new Random().nextLong();

    private final ImmutableSubject immutableSubject = new ImmutableSubject(resourceId);

    @Test
    public void testEquals() {
        Assert.assertTrue(immutableSubject.equals(immutableSubject));
        Assert.assertFalse(immutableSubject.equals(null));
        Assert.assertFalse(immutableSubject.equals(""));
        Assert.assertFalse(immutableSubject.equals(new ImmutableSubject(resourceId + 1)));
        Assert.assertTrue(immutableSubject.equals(new ImmutableSubject(resourceId)));
    }

    @Test
    public void testGetResourceId() {
        Assert.assertEquals(resourceId, immutableSubject.getResourceId());
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(immutableSubject.hashCode(), new ImmutableSubject(resourceId).hashCode());
    }

    @Test
    public void testToString() {
        String string = immutableSubject.toString();
        Assert.assertTrue(string.contains(ImmutableSubject.class.getSimpleName()));
        Assert.assertTrue(string.contains("" + resourceId));
    }
}
