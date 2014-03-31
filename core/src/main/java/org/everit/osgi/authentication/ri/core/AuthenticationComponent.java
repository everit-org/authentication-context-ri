/**
 * This file is part of org.everit.osgi.authentication.ri.core.
 *
 * org.everit.osgi.authentication.ri.core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.ri.core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.ri.core.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.ri.core;

import java.util.Hashtable;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.everit.osgi.authentication.api.AuthenticationService;
import org.everit.osgi.props.PropertyService;
import org.everit.osgi.resource.api.ResourceService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

@Component(name = AuthenticationServiceConstants.COMPONENT_NAME, metatype = true,
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = AuthenticationServiceConstants.PROP_RESOURCE_SERVICE_TARGET),
        @Property(name = AuthenticationServiceConstants.PROP_PROPERTY_SERVICE_TARGET)
})
public class AuthenticationComponent {

    @Reference
    private ResourceService resourceService;

    @Reference
    private PropertyService propertyService;

    private ServiceRegistration<AuthenticationService> authenticationServiceSR;

    @Activate
    public void activate(final BundleContext context, final Map<String, Object> componentProperties) {
        long defaultSubjectResourceId = initDefaultSubjectResourceId();
        Hashtable<String, Object> serviceProperties = new Hashtable<>(); // TODO define service properties
        AuthenticationService authenticationService = new AuthenticationServiceImpl(defaultSubjectResourceId);
        authenticationServiceSR =
                context.registerService(AuthenticationService.class, authenticationService, serviceProperties);
    }

    public void bindPropertyService(final PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    public void bindResourceService(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Deactivate
    public void deactivate() {
        if (authenticationServiceSR != null) {
            authenticationServiceSR.unregister();
            authenticationServiceSR = null;
        }
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

}
