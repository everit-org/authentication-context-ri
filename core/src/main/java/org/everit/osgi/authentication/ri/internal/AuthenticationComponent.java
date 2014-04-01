/**
 * This file is part of org.everit.osgi.authentication.ri.
 *
 * org.everit.osgi.authentication.ri is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.ri is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.ri.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.ri.internal;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.api.AuthenticatedAction;
import org.everit.osgi.authentication.api.AuthenticationService;
import org.everit.osgi.authentication.api.Subject;
import org.everit.osgi.authentication.ri.ImmutableSubject;
import org.everit.osgi.props.PropertyService;
import org.everit.osgi.resource.api.ResourceService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

@Component(name = AuthenticationConstants.COMPONENT_NAME, metatype = true, // FIXME remove immediate = true
        immediate = true,
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = AuthenticationConstants.PROP_RESOURCE_SERVICE_TARGET)
// FIXME @Property(name = AuthenticationConstants.PROP_PROPERTY_SERVICE_TARGET)
})
@Service
public class AuthenticationComponent implements AuthenticationService {

    @Reference
    private ResourceService resourceService;

    // FIXME @Reference
    private PropertyService propertyService;

    private final InheritableThreadLocal<ImmutableSubject> currentSubject =
            new InheritableThreadLocal<ImmutableSubject>();

    private ImmutableSubject defaultSubject;

    // FIXME remove
    private ServiceRegistration<PropertyService> propertyServiceSR;

    @Activate
    public void activate(final BundleContext context, final Map<String, Object> componentProperties) {
        // FIXME remove service registration
        propertyService = new PropertyService() {

            private final Map<String, String> props = new HashMap<>();

            @Override
            public void addProperty(final String arg0, final String arg1) {
                props.put(arg0, arg1);
            }

            @Override
            public String getProperty(final String arg0) {
                return props.get(arg0);
            }

            @Override
            public String removeProperty(final String arg0) {
                return props.remove(arg0);
            }

            @Override
            public String setProperty(final String arg0, final String arg1) {
                return props.put(arg0, arg1);
            }

        };
        propertyServiceSR = context.registerService(PropertyService.class, propertyService,
                new Hashtable<String, Object>());

        long defaultSubjectResourceId = initDefaultSubjectResourceId();
        defaultSubject = new ImmutableSubject(defaultSubjectResourceId);
    }

    public void bindPropertyService(final PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    public void bindResourceService(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Deactivate
    public void deactivate() {
        if (propertyServiceSR != null) {
            propertyServiceSR.unregister();
            propertyServiceSR = null;
        }
    }

    @Override
    public Subject getCurrentSubject() {
        ImmutableSubject immutableSubject = currentSubject.get();
        if (immutableSubject != null) {
            return immutableSubject;
        }
        return defaultSubject;
    }

    private long initDefaultSubjectResourceId() {
        long defaultSubjectResourceId;
        String defaultSubjectResourceIdProperty =
                propertyService.getProperty(AuthenticationService.PROP_DEFAULT_SUBJECT_RESOURCE_ID);
        if (defaultSubjectResourceIdProperty == null) {
            defaultSubjectResourceId = resourceService.createResource();
            propertyService.setProperty(
                    AuthenticationService.PROP_DEFAULT_SUBJECT_RESOURCE_ID, String.valueOf(defaultSubjectResourceId));
        } else {
            defaultSubjectResourceId = Long.valueOf(defaultSubjectResourceIdProperty).longValue();
        }
        return defaultSubjectResourceId;
    }

    @Override
    public void logout(final Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("subject cannot be null");
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T runAs(final Subject subject, final AuthenticatedAction<T> authenticatedAction) {
        if (subject == null) {
            throw new IllegalArgumentException("subject cannot be null");
        }
        if (authenticatedAction == null) {
            throw new IllegalArgumentException("authenticatedAction cannot be null");
        }
        ImmutableSubject immutableSubject = new ImmutableSubject(subject.getResourceId());
        currentSubject.set(immutableSubject);
        try {
            return authenticatedAction.run();
        } finally {
            currentSubject.set(defaultSubject);
        }
    }

}
