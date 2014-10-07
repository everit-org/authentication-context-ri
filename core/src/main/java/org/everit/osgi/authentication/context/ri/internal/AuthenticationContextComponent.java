/**
 * This file is part of Everit - Authentication Context RI.
 *
 * Everit - Authentication Context RI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Authentication Context RI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Authentication Context RI.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.context.AuthenticationContext;
import org.everit.osgi.authentication.context.AuthenticationPropagator;
import org.everit.osgi.authentication.context.ri.AuthenticationContextConstants;
import org.everit.osgi.props.PropertyManager;
import org.everit.osgi.resource.ResourceService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * The reference implementation of the {@link AuthenticationContext} and {@link AuthenticationPropagator} interfaces.
 */
@Component(name = AuthenticationContextConstants.SERVICE_FACTORYPID_AUTHENTICATION_CONTEXT, metatype = true,
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = Constants.SERVICE_DESCRIPTION,
                value = AuthenticationContextConstants.DEFAULT_SERVICE_DESCRIPTION),
        @Property(name = AuthenticationContextConstants.PROP_RESOURCE_SERVICE_TARGET),
        @Property(name = AuthenticationContextConstants.PROP_PROPERTY_MANAGER_TARGET)
})
@Service
public class AuthenticationContextComponent implements AuthenticationContext, AuthenticationPropagator {

    /**
     * The {@link ResourceService} used to initialize the resource of the default subject.
     */
    @Reference(bind = "setResourceService")
    private ResourceService resourceService;

    /**
     * The {@link PropertyManager} used to load/store the value of the
     * {@link AuthenticationContext#PROP_DEFAULT_RESOURCE_ID}.
     */
    @Reference(bind = "setPropertyManager")
    private PropertyManager propertyManager;

    /**
     * The Resource ID assigned to the actual thread.
     */
    private final ThreadLocal<Long> currentResourceId = new ThreadLocal<Long>();

    /**
     * The default Resource ID.
     */
    private long defaultResourceId;

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
        String defaultSubjectResourceIdProperty =
                propertyManager.getProperty(AuthenticationContextConstants.PROP_DEFAULT_RESOURCE_ID);
        if (defaultSubjectResourceIdProperty == null) {
            defaultResourceId = resourceService.createResource();
            propertyManager.addProperty(
                    AuthenticationContextConstants.PROP_DEFAULT_RESOURCE_ID, String.valueOf(defaultResourceId));
        } else {
            defaultResourceId = Long.valueOf(defaultSubjectResourceIdProperty).longValue();
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
        Objects.requireNonNull(authenticatedAction, "authenticatedAction cannot be null");
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

    public void setPropertyManager(final PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    public void setResourceService(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

}
