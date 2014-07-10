/**
 * This file is part of org.everit.osgi.authentication.context.ri.
 *
 * org.everit.osgi.authentication.context.ri is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.context.ri is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.context.ri.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.context.ri.internal;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.context.api.AuthenticationContext;
import org.everit.osgi.authentication.context.api.AuthenticationPropagator;
import org.everit.osgi.props.PropertyService;
import org.everit.osgi.resource.api.ResourceService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The reference implementation of the {@link AuthenticationContext} and {@link AuthenticationPropagator} interfaces.
 */
@Component(name = AuthenticationConstants.COMPONENT_NAME, metatype = true,
        immediate = true, // FIXME remove immediate true
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = AuthenticationConstants.PROP_RESOURCE_SERVICE_TARGET)
// FIXME @Property(name = AuthenticationConstants.PROP_PROPERTY_SERVICE_TARGET)
})
@Service
public class AuthenticationComponent implements AuthenticationContext, AuthenticationPropagator {

    /**
     * The {@link ResourceService} used to initialize the resource of the default subject.
     */
    @Reference
    private ResourceService resourceService;

    /**
     * The {@link PropertyService} used to load/store the value of the
     * {@link AuthenticationContext#PROP_DEFAULT_SUBJECT_RESOURCE_ID}.
     */
    // FIXME @Reference
    private PropertyService propertyService;

    /**
     * The Resource ID assigned to the actual thread.
     */
    private final ThreadLocal<Long> currentResourceId = new ThreadLocal<Long>();

    /**
     * The default Resource ID.
     */
    private long defaultResourceId;

    // FIXME remove
    private ServiceRegistration<PropertyService> propertyServiceSR;

    /**
     * The activate method if this OSGi component. It initializes the {@link #defaultResourceId}.
     *
     * @param context
     *            the bundle context
     * @param componentProperties
     *            the properties of this component
     */
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

        String defaultSubjectResourceIdProperty =
                propertyService.getProperty(AuthenticationContext.PROP_DEFAULT_RESOURCE_ID);
        if (defaultSubjectResourceIdProperty == null) {
            defaultResourceId = resourceService.createResource();
            propertyService.setProperty(
                    AuthenticationContext.PROP_DEFAULT_RESOURCE_ID, String.valueOf(defaultResourceId));
        } else {
            defaultResourceId = Long.valueOf(defaultSubjectResourceIdProperty).longValue();
        }
    }

    /**
     * The property binding method of {@link #propertyService}.
     *
     * @param ps
     *            the service to bind
     */
    public void bindPropertyService(final PropertyService ps) {
        propertyService = ps;
    }

    /**
     * The property binding method of {@link #resourceService}.
     *
     * @param rs
     */
    public void bindResourceService(final ResourceService rs) {
        resourceService = rs;
    }

    // FIXME remove
    @Deactivate
    public void deactivate() {
        if (propertyServiceSR != null) {
            propertyServiceSR.unregister();
            propertyServiceSR = null;
        }
    }

    @Override
    public long getCurrentResourceId() {
        Long resourceId = currentResourceId.get();
        if (resourceId == null) {
            return defaultResourceId;
        }
        return resourceId.longValue();
    }

    @Override
    public long getDefaultResourceId() {
        return defaultResourceId;
    }

    @Override
    public <T> T runAs(final long authenticatedResourceId, final Supplier<T> authenticatedAction) {
        if (authenticatedAction == null) {
            throw new IllegalArgumentException("authenticatedAction cannot be null");
        }
        Long localResourceId = currentResourceId.get();
        currentResourceId.set(authenticatedResourceId);
        T rval = null;
        try {
            rval = authenticatedAction.get();
        } finally {
            currentResourceId.set(localResourceId);
        }
        return rval;
    }

}
